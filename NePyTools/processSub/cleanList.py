#coding=utf-8
import os
import io
import time
import shutil

def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)

toProcessPathAuto = 'autosub_to_process/'
toProcessPathAutoEasy = 'autosub_to_process_easy/'
toProcessPathAutoHard = 'autosub_to_process_hard/'
toProcessPathSrt = 'srt_to_process/'
noSub = 'nosub/'
autosub = 'autosub/'
srt = 'srt/'

mkdir(toProcessPathAuto)
mkdir(toProcessPathSrt)
mkdir(noSub)
mkdir(autosub)
mkdir(srt)

def isFileDownloaded(vid) -> bool:
	for filename in os.listdir(toProcessPathAuto):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(toProcessPathAutoEasy):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(toProcessPathAutoHard):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(toProcessPathSrt):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(noSub):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(autosub):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	for filename in os.listdir(srt):
		if filename.endswith('.mp4') and filename.startswith(vid):
			return True
	return False

head = 'https://www.youtube.com/watch?v='

autoSubPath = autosub+"auto.txt"
fauto = io.open(autoSubPath, "r+")
fout = io.open(autosub+'auto_out.txt', "w+")
lines = fauto.readlines()
for line in lines:
	vid = line.replace(head,'')
	vid = vid.replace('\n', '')
	if not isFileDownloaded(vid):
		fout.write(line)
fout.close()

srtPath = srt+"srt.txt"
fSrt = io.open(srtPath, "r+")
fSrtOut = io.open(srt+'srt_out.txt', "w+")
lines = fSrt.readlines()
for line in lines:
	vid = line.replace(head,'')
	vid = vid.replace('\n', '')
	if not isFileDownloaded(vid):
		fSrtOut.write(line)
fSrtOut.close()



