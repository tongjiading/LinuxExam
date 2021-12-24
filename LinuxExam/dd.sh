#!/bin/bash
top -b -n 1 | grep java > test.txt
a=`awk '{print $9}' test.txt`
b=`echo "$a>20"|bc`

if [ $b -eq 1 ];then
   python3 miaomiao.py $a  
else
   echo false
fi


