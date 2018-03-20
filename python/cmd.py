from tempfile import TemporaryFile
import subprocess
from subprocess import PIPE
import os
import time
import sys
import datetime
import traceback

# ---------- Module Interface Functions ----------


def run_bg_log(log_path, *args):
    """
    Start the command as a background process and return immediately,
    stdout and stderr is redirected to log_path
     - The expansion (*) operator is used when passing sequence objects
       to prevent nesting a sequence within another
     - Logging requires a shell to handle the output redirection
    """
    log_path = _get_valid_log_path(log_path)
    cmd_and_pipe = _gen_cmd_with_log(log_path, *args)
    print('Running command:  ' + cmd_and_pipe)
    return subprocess.Popen(cmd_and_pipe, shell=True)  # Non-blocking, the shell does the IO redirection to file and returns a ref


def run_bg(*args, **kwargs):
    """
    Runs, then returns process object
    The expansion (*) operator is used when passing sequence objects to
    prevent nesting a sequence
    """
    print('run: args:' + str(args))

    if kwargs.get('shell') is True:
        p = subprocess.Popen(' '.join(args), **kwargs)
    else:
        p = subprocess.Popen(args, **kwargs)

    p.poll()  # This is a non blocking call
    return p


def run(*args, **kwargs):
    """
    Runs, waits for completion then returns stderr/ stdout and the return code
    PIPE can deadlock based on the child process output volume,
    uses tmp file instead
    The expansion (*) operator is used when passing sequence objects to
    prevent nesting a sequence
    """
    with TemporaryFile() as tmpfo, TemporaryFile() as tmpfe:

        print('run: args:' + str(args))

        if kwargs.get('shell') is True:
            p = subprocess.Popen(' '.join(args),    stdout=tmpfo, stderr=tmpfe, **kwargs)
        else:
            p = subprocess.Popen(args,              stdout=tmpfo, stderr=tmpfe, **kwargs)

        p.communicate()  # This is a blocking call
        return _resp(tmpfo, tmpfe), p.returncode


def is_running(p):
    """
    The child return code is set by poll()
    A None value indicates that the process hasn't terminated yet
    A negative value indicates that the child was terminated
    by signal (Unix only)
    """
    p.poll()
    if p.returncode is None:
        return True
    elif os.name == 'posix' and p.returncode < 0:
        return False

    return False


def keep_alive(fn, args):
    try:
        p = fn(*args)

        if type(p) is not subprocess.Popen:
            print("Invalid function supplied, it must return an object of type 'Popen'")
            sys.exit()

        while(True):
            if not is_running(p):
                print("Restarting process at: " + _datetime())
                print("sleeping first...")
                time.sleep(3600)
                p = fn(*args)
            time.sleep(5)
    except:
        traceback.print_exc()


# ---------- Module Utility Functions ----------


def _datetime():
    now = datetime.datetime.now()
    return now.strftime("%Y-%m-%d %H:%M")


def _resp(tmpfo, tmpfe):
    tmpfo.seek(0)
    tmpfe.seek(0)
    output = tmpfo.read()
    return output + tmpfe.read()


def _gen_cmd_with_log(log_path, *args):
    """
    This function redirects stderr, stdout and caters for platform differences
    """

    print('Command elements:  ' + str(args))
    print('Logging to:  ' + str(log_path))

    cmd = ""
    for arg in args:
        cmd = _concat_with_space(cmd, arg)

    if os.name == 'nt':
        if log_path is None or log_path == '':
            log_path = "null"
        cmd_and_pipe = cmd + " >> " + log_path + " 2>&1"  # Append stdout & stderr redirect
    else:
        if log_path is None or log_path == '':
            log_path = "/dev/null"
        cmd_and_pipe = cmd + " >> " + log_path + " 2>&1"  # Append stdout & stderr redirect

    return cmd_and_pipe


def _get_valid_log_path(log_path):
    """
    This function is essential for preventing driver startup failures due to
    log files being locked by other procs
    """
    log_path = _to_os_slashes(log_path)
    cnt = 0
    while _check_locks(log_path) is False:
        cnt = cnt + 1
        index = log_path.rfind(".")
        log_path = log_path[:index] + str(cnt) + log_path[index:]

    if cnt > 0:
        print("Lock found on file, redirecting output to " + log_path)

    return log_path


def _check_locks(log_path):
    if log_path is not None and os.path.exists(log_path):
        try:
            with open(log_path, "w") as f:
                f.write(" ")
                return True
        except:
            return False
    else:
        return True


def _to_os_slashes(fp):
    if os.name == 'nt':
        return _to_bck_slashes(fp)
    else:
        return _to_fwd_slashes(fp)


def _to_fwd_slashes(fp):
    if fp is not None:
        return fp.replace('\\', '/')
    else:
        return fp


def _to_bck_slashes(fp):
    if fp is not None:
        return fp.replace('/', '\\')
    else:
        return fp


def _concat_with_space(*args):
    tmp = ""
    for arg in args:
        tmp = tmp.strip() + ' ' + _get_empty(arg)
    return tmp


def _get_empty(arg):
    if arg is None or arg == '':
        return ''
    return arg


if __name__ == "__main__":
    """
    # --- Sync invocation Test ---
    output, code = run("dir", "/B", shell=True)
    print("output = " + output)
    print("code = " + str(code))

    # --- Async invocation Test ---
    p = run_bg_log("test.log", "long_job.bat")
    print("is_running = " + str(is_running(p)) + ", sleeping")
    time.sleep(20)
    print("is_running = " + str(is_running(p)) + ", sleeping")
    time.sleep(20)
    print("is_running = " + str(is_running(p)) + ", sleeping")

    # --- Test keepalive ----
    keep_alive(run_bg_log, ("test.log", "long_job.bat"))
    """
