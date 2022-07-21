#coding=utf-8
import io
from collections import OrderedDict

import realEngUtils
import srt_yy
import jsonpickle
import os
import os.path
import shutil
import RealEngGroupInfo

def generateGroupInfoFile(path, groupname):
    wordsFile = groupname +realEngUtils.infoFileTail;
    fa = []
    cc = ''
    nameList = []
    for filename in os.listdir(path):
        if filename.endswith('.mp4'):
            nameList.append(filename)

    indexDict = OrderedDict()
    indexList = []
    for filename in nameList:
        tt = filename.split("=")
        vid = tt[0]
        vv = vid.split('_')
        v1=int(vv[2])
        indexList.append(v1)
        indexDict[v1]=filename
    indexList.sort(reverse=False)

    for index in indexList:
        filename = indexDict[index]

        tt = filename.split("=")
        vv = RealEngGroupInfo.vidHead + ' ' + tt[0]
        title = '\n' + RealEngGroupInfo.titleHead + ' ' + tt[1].replace('.mp4', '')
        tt = '\n' + RealEngGroupInfo.typeHead + ' ' + realEngUtils.lessonDefaultType
        channel = '\n' + RealEngGroupInfo.channelHead + ' ' + realEngUtils.lessonDefaultChannel
        tag = '\n' + RealEngGroupInfo.tagHead + ' '
        lessonCamp = '\n' + RealEngGroupInfo.campHead + ' ' + realEngUtils.lessonDefaultCamp
        indexInCamp = '\n' + RealEngGroupInfo.indexInCampHead + ' ' + str(0)

        ww = '\n' + RealEngGroupInfo.wordListStart + '\n\n\n' + RealEngGroupInfo.wordListEnd
        vv = vv + tt + title + channel + tag + lessonCamp + indexInCamp + ww

        fa.append(vv)

    # fa.sort(reverse=False)

    for aa in fa:
        cc = cc + '\n' + aa + '\n\n\n\n\n'
    with open(path+wordsFile, 'w+') as outfile:
        outfile.write(cc)


###########parse xlsx
groupName = 'wave_2'
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'

from realEngCloudUtil import parseHuaweiCloudXlsx

generateGroupInfoFile(originPath, groupName)






