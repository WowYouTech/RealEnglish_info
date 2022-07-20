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
                copyfile(path + infoJson, outPath + infoJson)


originPath = "/Users/steveyang/EnglishAppProject/SnapVideos/wave_2/"
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

moveToAppFolder(originPath, assetPath)




