import cmd
import argparse

def clone(url):
    args = ["git", "clone", url]
    output, rc = cmd.run(*args, shell=True)
    print("{}: {}".format(rc, output))

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("url", help="The git repo URL", type=str)
    args = parser.parse_args()
    clone(args.url)
