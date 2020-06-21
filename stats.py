import matplotlib.pyplot as plt
import numpy as np
import csv
import scipy.stats as sp
import pandas as pd
from math import sqrt



x = []
time_array=[]
supertypes_array = []
calls_array = []
class_names = []

with open('log-20-06-50iteration.txt', 'r') as file:
    reader = csv.reader(file)
    for row in reader:
        # name, t, supertypes, resolveCalls
        class_name = row[0]
        t = row[1]
        # if class_name in class_map :
        #     class_map[class_name] = class_map[class_name].append(t)
        # else:
        #     l = []
        #     l.append(t)
        #     class_map[class_name] = l

        x.append(int(t) / 1000000) # ns -> ms 
        class_names.append(row[0])
        time_array.append(int(row[1]) / 1000000)
        supertypes_array.append(int(row[2]))
        calls_array.append(int(row[3]))
        
n = len(x)
average = sum(x) / n

# print('mean = ', mean)
s = 0
for i in x:
    s += (i - average) * (i - average) 

dispersion = s / n 
print('n = ', n)
print('average = ', average)
print('dispersion = ', dispersion)
print('sigma = ', sqrt(dispersion))



def printStats(df, key):
    
    print('Stats for key = ', key)
    print('mean =', df[key].mean())
    print('median = ' , df[key].median()) 
    # print('10th percentile = ' , df[key].quantile(0.1)) # 10th percentile
    print('90th percentile = ' ,df[key].quantile(0.9)) # same as median
    print('95th percentile = ' ,df[key].quantile(0.95)) # 90th percentile
    print('99th percentile = ' ,df[key].quantile(0.99)) # 90th percentile
    print('max =', max(df[key]), ', min = ', min(df[key]))
    print('----------------------------------------------------------')

# https://stackoverflow.com/questions/39581893/pandas-find-percentile-stats-of-a-given-column
df = pd.DataFrame({ 'time': time_array, 'classes': class_names, 'supertypes' : supertypes_array, 'calls': calls_array})

# time
printStats(df, 'time')
printStats(df, 'supertypes')
printStats(df, 'calls')



