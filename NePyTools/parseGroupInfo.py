#coding=utf-8
from collections import OrderedDict
import simplejson as json
import RealEngGroupInfo

##Generate group info json file & Generate words json files




wordEmptyStr = ';;'
chineseCode='zh-CN'
englishCode='en'
contextHead='??'
splitStr='!!!!'

def parseWords(wordLine) -> str:
    sa = wordLine.split(splitStr)
    wordList=[]
    for ss in sa:
        word = OrderedDict()
        wordSplits = ss.split('=')
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


def parseGroupFile(folderPath, filePath, outFilePath):
    data_list = []
    with open(filePath) as f:
        lines = f.readlines()
    i = 0
    data = OrderedDict()

    while i < len(lines):
        strLine = lines[i].strip()

        if strLine.startswith(RealEngGroupInfo.vidHead):
            data = OrderedDict()
            data['vid'] = strLine.replace(RealEngGroupInfo.vidHead, '').strip()

            if len(data['vid']) > 0:
                data_list.append(data)

            i += 1
        elif strLine.startswith(RealEngGroupInfo.typeHead):
            data['type'] = strLine.replace(RealEngGroupInfo.typeHead, '').strip()
            i += 1
        elif strLine.startswith(RealEngGroupInfo.titleHead):
            data['title'] = strLine.replace(RealEngGroupInfo.titleHead, '').strip()
            i += 1
        elif strLine.startswith(RealEngGroupInfo.channelHead):
            data['channel'] = strLine.replace(RealEngGroupInfo.channelHead, '').strip()
            i += 1
        elif strLine.startswith(RealEngGroupInfo.tagHead):
            data['tag'] = strLine.replace(RealEngGroupInfo.tagHead, '').strip()
            i += 1
        elif strLine.startswith(RealEngGroupInfo.wordListStart):
            i += 1
            data['wordItemStr']=''
            wordLines = ''
            wordStr = lines[i].strip()
            while i < len(lines) and not wordStr.startswith(RealEngGroupInfo.wordListEnd):
                if wordStr.__contains__('='):
                    wordLines += wordStr
                i += 1
                wordStr = lines[i].strip()

            if len(wordLines) > 0:
                wordItemStr = parseWords(wordLines)
                if len(wordItemStr) > 0:
                    data['wordItemStr'] = wordItemStr

        else:
            print('')
            i += 1

    j = json.dumps(data_list)

    for dd in data_list:
        ff =  folderPath + dd['vid'] + '=' + dd['title'] + '_info.txt'

        word = dd['wordItemStr']
        with open(ff, 'w+') as f:
            f.write(word)

    # Write to file
    with open(outFilePath, 'w+') as f:
        f.write(j)


groupName = 'wave_2'
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
filePath = originPath + groupName + '_info.txt'
outPath = originPath + groupName + '_info_json.txt'


from moveFilesToApp import moveToAppFolder

assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

parseGroupFile(originPath, filePath,outPath)
moveToAppFolder(originPath, assetPath)



