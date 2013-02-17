import sys
import json
import string


argversion = sys.argv

versionlist = argversion[1].split('R')

mcversion = versionlist[0]
mfrversion = versionlist[1]
if(len(versionlist) > 2):
	mfrversion = versionlist[1]+"R"+versionlist[2]

print (mfrversion)