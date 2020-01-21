import os
import autosub
import time
import shutil

def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)


def mymovefile(srcfile,dstfile):
    if not os.path.isfile(srcfile):
        print("%s not exist!"%(srcfile))
    else:
        fpath,fname=os.path.split(dstfile)    #分离文件名和路径
        if not os.path.exists(fpath):
            os.makedirs(fpath)                #创建路径
        shutil.move(srcfile,dstfile)          #移动文件
        print("move %s -> %s"%( srcfile,dstfile))

toProcessPath = '../autosub_to_process/'
mkdir(toProcessPath)

for filename in os.listdir('./'):
    if filename.endswith('.mp4'):
        print(filename)
        sub = filename.replace('.mp4', '.en.srt')
        thum = filename.replace('.mp4', '.jpg')
        print(thum)
        print(sub)
        exists = os.path.isfile(sub)
        if exists:
            print("sub exist")
        else:
            print("generate sub " + filename)
            subtitle_file_path = autosub.generate_subtitles(
                source_path=filename,
                concurrency=10,
                output=sub,
            )
            print("Subtitles file created at {}".format(subtitle_file_path))
            time.sleep(2)

        outputExist = os.path.isfile(sub)
        if outputExist:
            mymovefile(thum,toProcessPath+thum)
            mymovefile(filename, toProcessPath + filename)
            mymovefile(sub, toProcessPath + sub)




