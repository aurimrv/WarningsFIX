#!/bin/bash
##################################################################################
# Para executar, foneca o caminho onde se encontra os programas e as ferramentas
# do experimento
#
# Por exemplo, considerando que os programas e as ferramentas estejam dentro do 
# diretório 
# /home/alunoinf/Documentos/experimento
# 
# O script deve ser chamado como:
# run.sh /home/alunoinf/Documentos/experimento
##################################################################################

mkdir programs_analyzeds/ 
unzip -o $2 -d  programs_analyzeds/ 

        DIR_TMP=/home/auri/NetBeansProjects/WarningsFIX/tmp	
	PROG=/home/auri/NetBeansProjects/WarningsFIX/programs_analyzeds
        prj=$1
	mkdir $PROG/$prj
	TOOLS=/home/auri/NetBeansProjects/WarningsFIX/tools
        SCRIPTS=/home/auri/NetBeansProjects/WarningsFIX/scripts
        PARSER=/home/auri/NetBeansProjects/WarningsFIX/parser



		# Redefinindo para JDK 1.7
		export JAVA_HOME=$TOOLS/jdk1.7.0
                export PATH=$JAVA_HOME/bin:$PATH

#####################################################################################
#                              Executar ferramenta    Hammurapi                     #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodahammurapi=`date +%s`

	        # Executando Hammurapi
		#echo -e "\tHammurapi"
		# Preparando diretorio
                mkdir $PROG/$prj/outputs/ 
                mkdir $PROG/$prj/treemap/
                mkdir $PROG/$prj/treemap/suspection_rate 
                mkdir $PROG/$prj/treemap/tools 
                mkdir $PROG/$prj/treemap/warnings
                mkdir $TOOLS/Hammurapi-3.18.4/projects/$prj 
		cp -r $TOOLS/Hammurapi-3.18.4/projects/empty/* $TOOLS/Hammurapi-3.18.4/projects/$prj
		cp -r $PROG/$prj/source/* $TOOLS/Hammurapi-3.18.4/projects/$prj/src
                cp -r $PROG/$prj/lib/* $TOOLS/Hammurapi-3.18.4/projects/$prj/lib 
                cd $TOOLS/Hammurapi-3.18.4/projects/$prj
                ant clean 
	        ant -DprojectName=$prj  > /dev/null 2>&1
               
            	# Copiando relatorios para programa
		cp -rf $TOOLS/Hammurapi-3.18.4/projects/$prj/review/* $PROG/$prj/outputs/01-hammurapi-$prj 
                cp -rf $TOOLS/Hammurapi-3.18.4/projects/$prj/review/  $PROG/$prj/outputs/01-hammurapi-$prj 

              

terminiodaexecucaodahammurapi=`date +%s`
soma=`expr $terminiodaexecucaodahammurapi - $iniciodaexecucaodahammurapi`
resultado=`expr 10800 + $soma`
tempo=`date -d @$resultado +%H:%M:%S`
#echo Tempo gasto para execução da hammurapi: $tempo
info="O-tempo-gasto-para-execução-da-hammurapi"
info="$info-$tempo"
#bash $SCRIPTS/Store.sh $prj $info


#####################################################################################
#                              Executar ferramenta    Hammurapi                     #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdahammurapi=`date +%s`  

                      
                  				   
              cd $PROG/$prj/outputs/01-hammurapi-$prj/source             

   
              for HAMMU in $(find . -name '*.java.html')
                  do bash $SCRIPTS/Parser_Hammurapi2.sh  $PROG/$prj/outputs/01-hammurapi-$prj/source/$HAMMU $prj $HAMMU 
              done
                 
   
terminiodaexecucaodoparserdahammurapi=`date +%s`
soma2=`expr $terminiodaexecucaodoparserdahammurapi - $iniciodaexecucaodoparserdahammurapi`
resultado2=`expr 10800 + $soma1 + $soma2`
tempo2=`date -d @$resultado2 +%H:%M:%S`
#echo Tempo gasto para execução parser hammurapi: $tempo2 
info2="O-tempo-gasto-para-execução-parser-hammurapi"
info2="$info2-$tempo2"
info_total_hammurapi="$tempo2"
echo Tempo gasto para execução da ferramenta Hammurapi: $info_total_hammurapi
#bash $SCRIPTS/Store.sh $prj $info2

                        
#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

