import os, sys


def detectFileHeader(fpath):
    res = 0
    with open(fpath, 'r', encoding='utf-8') as f:
        if f.readline().strip() == '---':
            res = 1
            while 1:
                line = f.readline().strip()
                if line == '---':
                    break
                if 'hide: true' in line:
                    res = 2
    return res


def scanPostDir(post_dir):
    print('[Debug] post_dir=%s'%post_dir)
    unposted, hided = 0, 0
    for fn in os.listdir(post_dir):
        fpath = os.path.join(post_dir, fn)
        if os.path.isfile(fpath) and fn[-3:] == '.md':
            detect_res = detectFileHeader(fpath)
            if detect_res == 0:
                print('[+] Find an unposted blog: %s'%fpath)
                unposted +=1
            elif detect_res == 2:
                print('[+] Find a hided blog: %s'%fpath)
                hided += 1
    print('[$] Find %d unposted and %d hided blogs in total'%(unposted, hided))


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('[*] Usage: python scan_posts.py /PATH/TO/POST/DIR')
    else:
        try:
            scanPostDir(sys.argv[1])
        except Exception as e:
            print('[-] Exception occcured: %s'%e)