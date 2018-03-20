import os
import pip
import sys
import traceback
import time
import requests
import cmd

# ---------- Module Interface Functions ----------


def gitdwnld(api_url, limit, skip, file_path):

    # The repo ID at which to start cloning
    id = _get_last_id(file_path)

    clone_cnt = 0
    start = time.time()

    while(id < limit):
        with open("gitdwnld.log", "a") as lf:
            try:
                url = "{}?since={}".format(api_url, id)
                res = requests.get(url)
                _log_http_res(res)

                for repo in res.json():
                    _clone_repo(repo, res, lf)
                    break  # Only Take the first repo

            except:
                _log('"err":"{}",'
                     .format("ERR: Could not download repo with id:" +
                             str(id)), lf)

                strace = json_safe(traceback.format_exc())

                _log('"stacktrace":"{}",'.format(strace), lf)

            finally:
                time.sleep(10)

            id = id + skip
            _write_next_id(id, file_path)
            clone_cnt = clone_cnt + 1

            if clone_cnt == 58:
                _wait(start)
                start = time.time()
                clone_cnt = 0

            _log('"time":"{}"'   .format(time.strftime("%Y-%m-%d %H:%M")), lf)
            _log("},", lf)


# ---------- Module Utility Functions ----------


def _write_next_id(id, file_path):
    with open(file_path, "w") as f:
        f.write(str(id))


def _get_last_id(file_path):
    id = None
    try:
        if os.path.exists(file_path):
            with open(file_path, "r") as f:
                id = (int)(f.read())
        else:
            raise SystemError

        return id

    except SystemError:
        print("File not found: " + file_path)
        sys.exit()
    except ValueError:
        print("File '" + file_path + "' does not contain an integer")
        sys.exit()
    except:
        print("Could not process file: " + file_path)
        traceback.format_exc()
        sys.exit()


def _wait(start):
    min = 1800
    hour = 3600
    delta = time.time() - start
    zzz = hour - int(delta)

    if (zzz < min):
        zzz = min

    print("Sleeping for " + str(zzz) + " seconds...")
    time.sleep(zzz)


def _log(msg, logfile):
    try:
        print(msg)
    except UnicodeEncodeError as e:
        print("UniCodeError issue occured " + str(e))
        return
    logfile.write(msg+"\n")
    logfile.flush()


def _log_http_res(res):
    with open("./logs/" + str(time.time()) + "_gitdata.log", "wb") as fd:
        for chunk in res.iter_content(chunk_size=128):
            fd.write(chunk)


def json_safe(strin):
    if strin is None:
        return "None"
    else:
        return strin.replace('"', '\\"').replace('\n', '\\n')


def _clone_cmd(repo):
    # Dir names are not unique, if dir already exists clone to alt
    clone_url = repo["html_url"]
    clone_folder = clone_url.rsplit('/', 1)[-1]
    if(os.path.isdir(clone_folder)):
        clone_folder = clone_folder + "_" + str(int(time.time()))

    cmd = "git clone {}.git {} -v".format(clone_url, clone_folder)
    return cmd


def _clone_repo(repo, res, lf):
    if "id" not in repo:
        raise ValueError('Unstructured response received,' +
                         'HTTP code: ' + str(res.status_code))

    _log('{{"id":"{}",'.format(repo["id"]), lf)

    _log('"description":"{}",'
         .format(json_safe(repo["description"])), lf)

    _log('"uri":"{}",'           .format(repo["html_url"]), lf)

    _log('"time-start":"{}"'   .format(time.strftime("%Y-%m-%d %H:%M")), lf)

    p = cmd.run_bg(_clone_cmd(repo), shell=True)
    _monitor(p, lf)


def _monitor(p, lf):
    max_wait = 3600
    start = time.time()
    while(cmd.is_running(p)):
        delta = (int)(time.time() - start)
        if (delta > max_wait):
            _log('"max_wait":"exceeded"', lf)
        time.sleep(5)


if __name__ == "__main__":
    # The REST API URL from which to get the repo info
    api_url = "https://api.github.com/repositories"

    # The maximum repo ID
    limit = 10000000

    # The number of repo ID's to skip until next cloned one
    skip = 1000

    # The file used to store the last ID
    file_path = "gitdwnld.id"

    try:
        gitdwnld(api_url, limit, skip, file_path)

    except:
        print(traceback.format_exc())
