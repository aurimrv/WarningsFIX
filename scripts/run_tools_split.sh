#!/bin/bash

#formata codigo
#cd /programs/experimento/sir
#astyle -R *.java
#find -name *.orig -exec rm -rf {} \;

datainicial=`date +%s`
bash scripts/erase.sh 
bash scripts/Run_Hammurapi.sh $1 $2 
bash scripts/Run_FindBugs.sh $1 
bash scripts/Run_JCSC.sh $1
bash scripts/Run_CheckStyle.sh $1
bash scripts/Run_Jlint.sh $1
bash scripts/Run_PMD.sh $1
bash scripts/Run_ESCJAVA.sh $1
datafinal=`date +%s`
soma=`expr $datafinal - $datainicial`
resultado=`expr 10800 + $soma`
tempo=`date -d @$resultado +%H:%M:%S`
#echo Tempo gasto TOTAL: $tempo  

