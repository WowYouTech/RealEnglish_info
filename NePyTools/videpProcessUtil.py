

import os
import subprocess
from commonUtils import *
from moviepy.editor import *

ffmpegCmd = "/usr/local/bin/ffmpeg"

def getDurationOfVideo(filePath):
    from moviepy.editor import VideoFileClip
    clip = VideoFileClip(filePath)
    duration       = clip.duration
    # fps            = clip.fps
    # width, height  = clip.size
    return duration

def add_static_image_to_audio(image_path, audio_path, output_path):
    audio_clip = AudioFileClip(audio_path)
    image_clip = ImageClip(image_path)
    video_clip = image_clip.set_audio(audio_clip)
    video_clip.duration = audio_clip.duration
    video_clip.fps = 1
    video_clip.write_videofile(output_path)


def convertMp4WithImage(ff):
    videoFile = originPath + ff + '.mp4'
    videoOutFile = originPath + ff + '_image.mp4'
    image_to_convert = originPath + ff + '.jpeg'

    video_to_convert = originPath + 'video_to_convert.mp4'
    audio_to_write = originPath + 'audio_to_write.wav'
    mp3_to_write = originPath + 'audio_to_write.mp3'
    copyfile(videoFile, video_to_convert)

    video = VideoFileClip(os.path.join("path", "to", video_to_convert))
    video.audio.write_audiofile(os.path.join("path", "to", mp3_to_write))

    add_static_image_to_audio(image_to_convert, mp3_to_write, videoOutFile)
