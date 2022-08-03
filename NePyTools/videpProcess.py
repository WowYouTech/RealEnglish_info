

from videpProcessUtil import *


import imageio.v3 as iio

def imageFromVideo(path, name):
    videoFile = path + name + ".mp4"
    imageFile = path + name + ".jpg"
    for idx, frame in enumerate(iio.imiter(videoFile)):
        iio.imwrite(imageFile, frame)
        break
def createImageForVideos(path):
    for filename in os.listdir(path):
        if filename.endswith('.mp4'):
            name = filename.replace('.mp4','')
            imageFromVideo(path,name)

#Run
groupIndex = 2
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/' + groupName +'/'

# ff = 'short_1_0=😇Winter Olympic gold medalist interviewed over radio'
# convertMp4WithImage(ff)

createImageForVideos(originPath)
