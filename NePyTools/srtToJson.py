#coding=utf-8
import io

import srt_yy
import jsonpickle
import os
import os.path
import shutil

def mymovefile(srcfile,dstfile):
    if not os.path.isfile(srcfile):
        print("%s not exist!"%(srcfile))
    else:
        fpath,fname=os.path.split(dstfile)    #分离文件名和路径
        if not os.path.exists(fpath):
            os.makedirs(fpath)                #创建路径
        shutil.move(srcfile,dstfile)          #移动文件
        print("move %s -> %s"%( srcfile,dstfile))

def copyfile(srcfile,dstfile):
    if not os.path.isfile(srcfile):
        print("%s not exist!"%(srcfile))
    else:
        fpath,fname=os.path.split(dstfile)
        if not os.path.exists(fpath):
            os.makedirs(fpath)
        shutil.copyfile(srcfile,dstfile)
        print("copy %s -> %s"%( srcfile,dstfile))

class SubItem(object):

    def __init__(self, index, start, end, content, proprietary=""):
        self.index = index
        self.start = start
        self.end = end
        self.content = content
        self.proprietary = proprietary

def parseSrt(path):
    f = io.open(path, "r+")
    s = f.read()
    subs = list(srt_yy.parse(s))

    subJson = jsonpickle.encode(subs, unpicklable=False)
    f.close()
    return subJson

def convertSrtFolder(path):
    for filename in os.listdir(path):
        if filename.endswith('.srt'):
            outFileName = filename.replace('.srt','.txt')
            subJson = parseSrt(path+filename)
            ss = str(subJson)
            print(ss)
            with open(path+outFileName, 'w+') as outfile:
                outfile.write(ss)


def moveToAppFolder(path, outPath):
    for filename in os.listdir(path):
        if filename.endswith('.mp4'):
            print(filename)
            videoFile = filename
            thum = filename.replace('.mp4', '.jpg')
            subJson = filename.replace('.mp4', '.txt')
            infoJson = filename.replace('.mp4', '_info.txt')
            print(thum)
            print(subJson)

            exists = os.path.isfile(subJson)
            if exists:
                print("sub exist")
            else:
                print("sub not exist")
                copyfile(path+filename, outPath + videoFile)
                copyfile(path+thum, outPath + thum)
                copyfile(path+subJson, outPath + subJson)
                copyfile(path + subJson, outPath + infoJson)


# srtPath = "/Users/steveyang/Data_NE_720p/Content/autosub_Sub_Done_easy/33.srt"
# parseSrt(srtPath)

# originPath = "/Users/steveyang/EnglishAppProject/EnglishAppContent/Data_NE_720p/Content/srt_sub_done/"
# originPath = "/Users/steveyang/EnglishAppProject/ne_android_dev/appRealEnglish/src/main/assets/contents/"
originPath = "/Users/steveyang/EnglishAppProject/SnapVideos/wave_2/"
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"


# convertSrtFolder(originPath)

moveToAppFolder(originPath, assetPath)




