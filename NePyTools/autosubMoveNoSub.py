#coding=utf-8

import os
import os.path
import shutil

def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)

nosubPath = '../nosub/'

mkdir(nosubPath)

def mymovefile(srcfile,dstfile):
	if not os.path.isfile(srcfile):
		print("%s not exist!"%(srcfile))
	else:
		fpath,fname=os.path.split(dstfile)
		if not os.path.exists(fpath):
			os.makedirs(fpath)
		shutil.move(srcfile,dstfile)
		print("move %s -> %s"%( srcfile,dstfile))

for filename in os.listdir('./'):
	if filename.endswith('.mp4'):
		print(filename)
		thum = filename.replace('.mp4','.jpg')
		sub = filename.replace('.mp4', '.en.srt')
		print(thum)
		print(sub)

		exists = os.path.isfile(sub)
		if exists:
			print("sub exist")
		else:
			print("sub not exist")
			mymovefile(filename,nosubPath+filename)
			mymovefile(thum, nosubPath + thum)

