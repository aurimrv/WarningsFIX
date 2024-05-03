#!/bin/bash

#formata codigo
#cd /programs/experimento/sir
#astyle -R *.java
#find -name *.orig -exec rm -rf {} \;

datainicial=`date +%s`
bash scripts/erase.sh 
bash scripts/run-modificado.sh $1 $2 
datafinal=`date +%s`
soma=`expr $datafinal - $datainicial`
resultado=`expr 10800 + $soma`
tempo=`date -d @$resultado +%H:%M:%S`
#echo Tempo gasto TOTAL: $tempo  

