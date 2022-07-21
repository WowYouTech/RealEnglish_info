#coding=utf-8
import io

import realEngUtils
import srt_yy
import jsonpickle
import os
import os.path
from commonUtils import copyfile


def moveToAppFolder(path, outPath):
    for filename in os.listdir(path):
        if filename.endswith('.mp4'):
            print(filename)
            videoFile = filename
            thum = filename.replace('.mp4', '.jpg')
            subJson = filename.replace('.mp4', '.txt')
            infoJson = filename.replace('.mp4', realEngUtils.infoJsonTail)
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

# originPath = "/Users/steveyang/EnglishAppProject/SnapVideos/wave_2/"
# assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"
# moveToAppFolder(originPath, assetPath)




