#coding=utf-8
import io

import srt_yy
import jsonpickle
import os
import os.path
import shutil
import RealEngGroupInfo

def generateGroupInfoFile(path, groupname):
    wordsFile = groupname +'_info.txt';
    fa = []
    cc = ''
    for filename in os.listdir(path):
        if filename.endswith('.mp4'):
            tt = filename.split("=")
            vv = RealEngGroupInfo.vidHead + ' ' + tt[0]
            title = '\n' + RealEngGroupInfo.titleHead + ' '+ tt[1].replace('.mp4','')
            tt = '\n'+RealEngGroupInfo.typeHead+' '+'snap_video'
            channel = '\n'+RealEngGroupInfo.channelHead+' '+'funny'
            tag = '\n'+RealEngGroupInfo.tagHead + ' '
            ww = '\n'+RealEngGroupInfo.wordListStart + '\n\n\n' + RealEngGroupInfo.wordListEnd
            vv = vv + tt + title + channel + tag + ww

            fa.append(vv)

    fa.sort(reverse=False)
    for aa in fa:
        cc = cc + '\n' + aa + '\n\n\n\n\n'
    with open(path+wordsFile, 'w+') as outfile:
        outfile.write(cc)


###########parse xlsx
groupName = 'wave_2'
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'

generateGroupInfoFile(originPath, groupName)

# generateWordsFiles(originPath)




