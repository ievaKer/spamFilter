#!/usr/bin/env python

# Change last element in list to identify if email is spam or not!

import base64
import re
import quopri
import sys

def emailLength(msg):
    count = 0
    for i in msg:
        count += (i != '\n' and i != " ")
    return count

def numberOfLines(msg):
    count = 0
    for i in msg:
        count += (i == '\n')
    return count

def senderLength(sender):
    return len(sender.strip())
    
def titleLength(title):
    return len(title.strip())

def upperLowerCount(text):
    upper = 0
    lower = 0
    for i in text:
        if (i.islower()):
            lower += 1
        elif (i.isupper()):
            upper += 1
    return (upper, lower)

def countImages(html):
    x = re.findall("<img [^>]*>", html)
    if x is None:
        return 0
    return len(x)

def countLinks(html):
    x = re.findall("<a [^>]*href=[^>]*>", html)
    if x is None:
        return 0
    return len(x)

def countNumbers(text):
    x = re.findall("[0-9]", text)
    return len(x)

def countLithuanianLetters(text):
    x = re.findall("[ąčęėįšųūžĄČĘĖĮŠŲŪŽ]", text)
    return len(x)

def parseSubject(input):
    x = re.findall("=\?UTF-8\?B\?(.*?)\?=", input)
    if x is None:
        return input

    final = input
    for encoded in x:
        decoded = str(base64.b64decode(encoded), 'utf-8')
        final = re.sub("=\?UTF-8\?B\?.*\?=", decoded, final)

    return final.strip()
    
def parseQuotedPrintable(text):
    decoded = quopri.decodestring(text)
    return str(decoded, 'utf-8')

def countWords(text):
    return len(re.findall(r'\w+', text))

def countExclamationPoints(text):
    return len(re.findall('!', text))

def countCommas(text):
    return len(re.findall(',', text))

def countDots(text):
    return len(re.findall('[.]', text))

def hasMyName(text):
    x = re.search("[iI][eE][vV][aA]|[kK][eE][rR][sSšŠ][eE][vV][iI][cCčČ][iI][uUūŪ][tT][eEėĖ]", text)
    return x is not None

def isTicket(text):
    x = re.search("biliet|rezerva|apartament|butas|buto|butą|nuom|ticket|booking", text)
    return x is not None

def isConfirmation(text):
    x = re.search("užsaky[mt]|patvirtin|siunt|pristat|billed|order|sąskait|dokument|registrac|slaptažod|document|password|parašas|form|sutart|subscription|confirm|pažym|visa", text, re.IGNORECASE)
    return x is not None

def hasPeople(text):
    x = re.search("savanor|au[šs]rin[eė]|juozas|edvinas|saul[eė]|tomas|matas|rita|rasa|tautvydas|salomeja|kristina|eimantė|jonas|titas|njuspeip|erasmus", text, re.IGNORECASE)
    return x is not None

def isAd(text):
    x = re.search("unsubscribe|newsletter|atsisakyti|atsisakymas|naujienlaiškio|naujienlaiškis|prenumerata|prenumeratos|sale| discount|now available|išpardavimas|akcija|akcijos|nuolaida|nuolaidos", text, re.IGNORECASE)
    return x is not None

def timeOfDay(time):
    x = re.search("([0-9]{2}):[0-9]{2}:[0-9]{2}", time)
    hour = int(x.groups()[0])
    if hour < 6:
        return "NIGHT"
    if hour < 12:
        return "MORNING"
    if hour < 18:
        return "AFTERNOON"
    return "EVENING"

def isForwardedOrReplied(references):
    x = re.findall("<.*?>", references)
    return len(x) > 1

filename = sys.argv[1]
file = open(filename, 'r')
file = file.readlines()

subject = ""
from_ = ""
date = ""
contents = ['', '']
currContent = 0
hasAttachments = False
hasPlainText = False
hasHtml = False
hasSignature = False
isReference = False
references = ""

for line in file:
    if line.startswith("Subject: "):
        subject = parseSubject(line.partition(' ')[2])
    if line.startswith("From: "):
        from_ = parseSubject(line.partition(' ')[2])
    if line.startswith("Date: "):
        date = line.partition(' ')[2]
    
    if line.startswith("Mime-version:"):
        isReference = False
    
    if line.startswith("References: ") or isReference:
        references += line
        isReference = True
    
    if line.startswith("Content-type: text/plain"):
        hasPlainText = True
        currContent = 1
    
    if line.startswith("Content-type: text/html"):
        hasHtml = True
        currContent = 2
    
    if line.startswith("Content-type: application") or line.startswith("Content-type: audio") or line.startswith("Content-type: image") or line.startswith("Content-type: video"):
        currContent = 0
        hasAttachments = True
    
    if currContent == 1:
        contents[0] += line
        if line.startswith("--=20"):
            hasSignature = True
    
    if currContent == 2:
        contents[1] += line

content = parseQuotedPrintable('\n'.join(contents[0].split("\n")[5:-2]).strip())
(upper, lower) = upperLowerCount(content)

people = hasPeople(from_)
if not people:
    people = hasPeople(content)

results = [
    emailLength(content),
    numberOfLines(content),
    senderLength(from_),
    titleLength(subject),
    upper,
    lower,
    countImages(contents[1]),
    countLinks(contents[1]),
    countNumbers(content),
    countLithuanianLetters(content),
    hasAttachments,
    countExclamationPoints(content),
    countWords(content),
    hasPlainText,
    hasHtml,
    countCommas(content),
    countDots(content),
    people,
    isForwardedOrReplied(references),
    timeOfDay(date),
    isConfirmation(content),
    isAd(content),
    hasMyName(content),
    isTicket(content),
    hasSignature,
    True # is not spam!
]

print(','.join(map(str, results)))
