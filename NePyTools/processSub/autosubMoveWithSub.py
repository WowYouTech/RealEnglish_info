#coding=utf-8

import os
import os.path
import shutil


def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)

def mymovefile(srcfile,dstfile):
	if not os.path.isfile(srcfile):
		print("%s not exist!"%(srcfile))
	else:
		fpath,fname=os.path.split(dstfile)
		if not os.path.exists(fpath):
			os.makedirs(fpath)
		shutil.move(srcfile,dstfile)
		print("move %s -> %s"%( srcfile,dstfile))

def moveAllFiles(dstPath):
	mkdir(dstPath)
	toProcessPath_zh = dstPath + 'zh-Hans/'
	mkdir(toProcessPath_zh)
	for filename in os.listdir('../'):
		if filename.endswith('.mp4'):
			print(filename)
			thum = filename.replace('.mp4','.jpg')
			sub_en = filename.replace('.mp4', '.en.srt')
			sub_en_GB = filename.replace('.mp4', '.en-GB.srt')
			sub_en_US = filename.replace('.mp4', '.en-US.srt')
			sub_en_CA = filename.replace('.mp4', '.en-CA.srt')
			sub_en_IN = filename.replace('.mp4', '.en-IN.srt')
			sub_en_AU = filename.replace('.mp4', '.en-AU.srt')
			sub_en_NZ = filename.replace('.mp4', '.en-NZ.srt')
			sub_ch = filename.replace('.mp4', '.zh-Hans.srt')

			subSource = ''
			exists = False

			if os.path.isfile(sub_en):
				subSource = sub_en
				exists = True
			if os.path.isfile(sub_en_GB):
				subSource = sub_en_GB
				exists = True
			elif os.path.isfile(sub_en_US):
				subSource = sub_en_US
				exists = True
			elif os.path.isfile(sub_en_CA):
				subSource = sub_en_CA
				exists = True
			elif os.path.isfile(sub_en_IN):
				subSource = sub_en_IN
				exists = True
			elif os.path.isfile(sub_en_AU):
				subSource = sub_en_AU
				exists = True
			elif os.path.isfile(sub_en_NZ):
				subSource = sub_en_NZ
				exists = True

			if exists:
				print("sub en- exist")
				mymovefile(filename, dstPath + filename)
				mymovefile(thum, dstPath + thum)
				mymovefile(subSource, dstPath + sub_en)
				mymovefile(sub_ch, toProcessPath_zh + sub_ch)
			else:
				print("sub not exist")

toPath = '../autosub_to_process/'
moveAllFiles(toPath)