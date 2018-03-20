import cmd

if __name__ == "__main__":
    cmd.keep_alive(cmd.run_bg_log, ("gitdwnld_runner.log", "python gitdwnld.py"))
