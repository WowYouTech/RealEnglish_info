#coding=utf-8
import os
from collections import OrderedDict
import simplejson as json
import RealEngGroupInfo
import io
from collections import OrderedDict
from videpProcessUtil import *

import realEngUtils
import srt_yy
import jsonpickle
import os
import os.path
import shutil
import RealEngGroupInfo


wordEmptyStr = ';;'
chineseCode='zh-CN'
englishCode='en'
contextHead='??'
wordItemSplitStr='!!!!'
wordContentSplitStr='='


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
        duration = getDurationOfVideo(path + filename)
        dd = int(duration)
        mm = int(dd / 60)
        ss = int(dd % 60)
        ds ="{:02}:{:02}".format(mm, ss)

        tt = filename.split("=")
        vid = tt[0]
        fileTitle = tt[1].replace('.mp4', '')
        contentStr=''
        for lineKey in RealEngGroupInfo.lessonEditElementList:
            if lineKey == RealEngGroupInfo.vid:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + vid
            elif lineKey == RealEngGroupInfo.title:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + fileTitle
            elif lineKey == RealEngGroupInfo.type:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + realEngUtils.lessonDefaultType
            elif lineKey == RealEngGroupInfo.channel:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + realEngUtils.lessonDefaultChannel
            elif lineKey == RealEngGroupInfo.lessonCamp:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + realEngUtils.lessonDefaultCamp
            elif lineKey == RealEngGroupInfo.indexInCamp:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + str(0)
            elif lineKey == RealEngGroupInfo.duration:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr  + ds
            else:
                lineStr = '\n' + lineKey + RealEngGroupInfo.splitStr

            contentStr += lineStr

        ww = '\n' + RealEngGroupInfo.wordListStart + '\n\n\n' + RealEngGroupInfo.wordListEnd
        contentStr += ww
        fa.append(contentStr)

    for aa in fa:
        cc = cc + '\n' + aa + '\n\n\n\n\n'
    with open(path+wordsFile, 'w+') as outfile:
        outfile.write(cc)


def parseWords(wordLine) -> str:
    sa = wordLine.split(wordItemSplitStr)
    wordList=[]
    for ss in sa:
        word = OrderedDict()
        wordSplits = ss.split(wordContentSplitStr)
        word['matchStr']=''

        if len(wordSplits) >= 5 and len(wordSplits[0]) > 0:

            word['matchStr'] = wordSplits[0]

            if wordSplits[1].startswith('['):
                word['phoneticStr'] = wordSplits[1]
            else:
                word['phoneticStr']=''

            transList = []
            if wordSplits[2] == wordEmptyStr:
                wordSplits[2]
            else:
                tran = OrderedDict()
                tran['languageCode'] = englishCode
                tran['localStr'] = wordSplits[2]
                transList.append(tran)
            if wordSplits[3] == wordEmptyStr:
                wordSplits[3]
            else:
                tran = OrderedDict()
                tran['languageCode'] = chineseCode
                tran['localStr'] = wordSplits[3]
                transList.append(tran)

            word['translatedItems'] = transList
            word['contextStr'] = wordSplits[4].replace(contextHead,'')

        if len(word['matchStr']) > 0:
            wordList.append(word)

    jj = json.dumps(wordList)
    print(jj)
    return jj


def parseGroupInfoToJson(folderPath, filePath, outFilePath):
    data_list = []
    with open(filePath) as f:
        lines = f.readlines()
    i = 0
    data = OrderedDict()

    while i < len(lines):
        strLine = lines[i].strip()
        if len(strLine) == 0:
            i += 1
            continue

        heads = strLine.split(RealEngGroupInfo.splitStr)
        if len(heads) == 0:
            i += 1
            continue
        lineKey = heads[0].strip()
        if len(lineKey) == 0:
            i += 1
            continue

        if lineKey == (RealEngGroupInfo.wordListStart):
            i += 1
            data['wordItemStr'] = ''
            wordLines = ''
            wordStr = lines[i].strip()
            while i < len(lines) and not wordStr.startswith(RealEngGroupInfo.wordListEnd):
                if len(wordStr) > 0:
                    if len(wordLines) > 0 and not wordStr.startswith(wordItemSplitStr):
                        wordLines += '\n' + wordStr
                    else:
                        wordLines += wordStr
                i += 1
                wordStr = lines[i].strip()
            if len(wordLines) > 0:
                wordItemStr = parseWords(wordLines)
                if len(wordItemStr) > 0:
                    data['wordItemStr'] = wordItemStr
            continue

        if not RealEngGroupInfo.lessonEditElementList.__contains__(lineKey):
            i += 1
            continue
        lineContent=''
        if len(heads) >= 2:
            lineContent = heads[1].strip()

        if lineKey == RealEngGroupInfo.vid:
            data = OrderedDict()
            data[lineKey] = lineContent
            if len(data[lineKey]) > 0:
                data_list.append(data)
            i += 1
            continue

        if lineKey == RealEngGroupInfo.indexInCamp:
            if len(lineContent) == 0:
                data['indexInCamp'] = 0
            else:
                data['indexInCamp'] = int(lineContent)
            i += 1
            continue

        if RealEngGroupInfo.lessonEditElementList.__contains__(lineKey):
            data[lineKey] = lineContent
            i += 1
            continue

    j = json.dumps(data_list)

    fileTitleDict = OrderedDict()
    for filename in os.listdir(folderPath):
        if filename.endswith('.mp4') and filename.__contains__(RealEngGroupInfo.fileNameSplitStr):
            sp = filename.split(RealEngGroupInfo.fileNameSplitStr)
            vid = sp[0]
            name = sp[1].replace('.mp4','')
            fileTitleDict[vid]=name

    for dd in data_list:
        vid = dd['vid']
        ff =  folderPath + vid + '=' + fileTitleDict[vid] + '_info.txt'
        word = dd['wordItemStr']
        with open(ff, 'w+') as f:
            f.write(word)

    # Write to file
    with open(outFilePath, 'w+') as f:
        f.write(j)



cloudKeyList = ['vid', 'coverUrl','contentUrl','duration']
infoKeyList = ['vid', 'type', 'title', 'channel', 'tag', 'wordItemStr']
keyVid = 'vid'
keySubStr = 'subStr'
cloudInfoTail='_cloud.txt'
infoJsonTail='_info_json.txt'
infoFileTail='_info.txt'
lessonsJsonTail='_lessons.txt'

lessonDefaultType='snap_video'
lessonDefaultChannel='funny'
lessonDefaultCamp='hot'

def generateLessonsHuawei(path, groupIndex, name) -> []:
    cloudInfoFile = path + name + cloudInfoTail
    groupInfoFile = path + name + infoJsonTail
    outFilePath = path + name + lessonsJsonTail
    cloudInfoJsonList=[]

    if os.path.isfile(cloudInfoFile):
        f = io.open(cloudInfoFile, "r+")
        s = f.read()
        cloudInfoJsonList = json.loads(s)

    f = io.open(groupInfoFile, "r+")
    s = f.read()
    groupInfoJsonList = json.loads(s)

    dataList = []

    itemsDict = OrderedDict()
    srtDicts = OrderedDict()


    for filename in os.listdir(path):
        if filename.endswith('.mp4') and filename.__contains__(RealEngGroupInfo.fileNameSplitStr):
            srtFileName = filename.replace('.mp4','.txt')
            srtPath = path + srtFileName

            f = io.open(srtPath, "r+")
            subStr = f.read()

            srtDict = OrderedDict()
            srtDict[keySubStr]=subStr
            vid = srtFileName.split(RealEngGroupInfo.fileNameSplitStr)[0]
            srtDicts[vid] = srtDict

    for ci in cloudInfoJsonList:
        vid = ci[keyVid]
        itemsDict[vid] = ci
    for gi in groupInfoJsonList:
        vid = gi[keyVid]
        if len(itemsDict) > 0:
            mm = {**gi, **itemsDict[vid], **srtDicts[vid]}
        else:
            mm = {**gi, **srtDicts[vid]}

        mm['groupIndex']=groupIndex
        dataList.append(mm)

    ss = json.dumps(dataList)

    with open(outFilePath, 'w+') as f:
        f.write(ss)

    return dataList

import urllib.parse

def generateLessons(path, groupIndex, name) -> []:
    groupInfoFile = path + name + infoJsonTail
    outFilePath = path + name + lessonsJsonTail


    f = io.open(groupInfoFile, "r+")
    s = f.read()
    groupInfoJsonList = json.loads(s)

    dataList = []

    itemsDict = OrderedDict()

    srtDicts = OrderedDict()

    for filename in os.listdir(path):
        if filename.endswith('.mp4') and filename.__contains__(RealEngGroupInfo.fileNameSplitStr):
            name = filename.replace('.mp4','')
            srtFileName = filename.replace('.mp4','.txt')
            srtPath = path + srtFileName

            f = io.open(srtPath, "r+")
            subStr = f.read()

            srtDict = OrderedDict()
            srtDict[keySubStr]=subStr
            vid = srtFileName.split(RealEngGroupInfo.fileNameSplitStr)[0]
            srtDicts[vid] = srtDict

            pp = 'https://wowenglish.blob.core.windows.net/' + 'wave' + str(groupIndex) + '/' + name
            ci = OrderedDict()
            itemsDict[vid] = ci
            ci['contentUrl'] = pp + '.mp4'
            ci['coverUrl'] = pp + '.jpg'


    for gi in groupInfoJsonList:
        vid = gi[keyVid]
        mm = {**gi, **itemsDict[vid], **srtDicts[vid]}

        mm['groupIndex']=groupIndex
        dataList.append(mm)

    ss = json.dumps(dataList)

    with open(outFilePath, 'w+') as f:
        f.write(ss)

    return dataList

# groupName = 'wave_2'
# originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
# filePath = originPath + groupName + '_info_e.txt'
# outPath = originPath + groupName + '_info_json.txt'
# assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

# parseGroupInfoToJson(originPath, filePath,outPath)
# moveToAppFolder(originPath, assetPath)




