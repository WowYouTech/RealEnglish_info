#coding=utf-8

import os
import os.path
import shutil

from srtMoveResultsWithSub import mkdir

nosubPath = '../nosub/'

mkdir(nosubPath)

def mymovefile(srcfile,dstfile):
    if not os.path.isfile(srcfile):
        print("%s not exist!"%(srcfile))
    else:
        fpath,fname=os.path.split(dstfile)    #分离文件名和路径
        if not os.path.exists(fpath):
            os.makedirs(fpath)                #创建路径
        shutil.move(srcfile,dstfile)          #移动文件
        print("move %s -> %s"%( srcfile,dstfile))

for filename in os.listdir('../'):
	if filename.endswith('.mp4'):
		print(filename)
		thum = filename.replace('.mp4','.jpg')
		sub = filename.replace('.mp4', '.en.srt')
		sub_ch = filename.replace('.mp4', '.zh-Hans.srt')
		print(thum)
		print(sub)
		print(sub_ch)

		exists = os.path.isfile(sub)
		if exists:
			print("sub exist")
		else:
			print("sub not exist")
			mymovefile(filename,nosubPath+filename)
			mymovefile(thum, nosubPath + thum)
			mymovefile(thum, nosubPath + sub_ch)


