# coding=utf-8
import os

import ffmpy
import moviepy.editor as mp
import cv2

contentPath = '/Users/steveyang/NativeEnglish/NE_posts/'

# for filename in os.listdir(contentPath):
#
#     if filename.endswith('.gif'):
#
#         print(filename)
#
#         toFileName = filename.replace('.gif', '.mp4')
#
#
#         # ff = ffmpy.FFmpeg(
#         #     inputs = {contentPath+filename: None},
#         #     outputs = {contentPath+toFileName: None})
#         # ff.run()
#
#         clip = mp.VideoFileClip(contentPath+filename)
#         clip.write_videofile(contentPath+toFileName)


for filename in os.listdir(contentPath):

    if filename.endswith('.jpg') or filename.endswith('.png') \
            or filename.endswith('.JPG') or filename.endswith('.jpeg')\
            :
        print(filename)

        toFileName = os.path.splitext(filename)[0]
        toFileName = toFileName + '.mp4'

        fromPath = contentPath + filename
        toPath = contentPath + toFileName
        fromPath = fromPath.replace(" ", "\\ ")
        toPath = toPath.replace(" ", "\\ ")

        os.system("ffmpeg -r 1 -i " + fromPath + " -vcodec mpeg4 -y " + toPath)
