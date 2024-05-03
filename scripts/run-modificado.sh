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

        DIR_TMP=~/workspace/WarningsFIX/tmp	
	PROG=~/workspace/WarningsFIX/programs_analyzeds
        prj=$1
	TOOLS=~/workspace/WarningsFIX/tools
        SCRIPTS=~/workspace/WarningsFIX/scripts
        PARSER=~/workspace/WarningsFIX/parser



		# Redefinindo para JDK 1.7
		#export JAVA_HOME=$TOOLS/jdk1.7.0
                #export PATH=$JAVA_HOME/bin:$PATH

#####################################################################################
#                              Executar ferramenta    Hammurapi                     #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodahammurapi=`date +%s`

	        # Executando Hammurapi
		#echo -e "\tHammurapi"
		# Preparando diretorio
                mkdir $PROG/$prj/outputs/ 
                mkdir $TOOLS/Hammurapi-3.18.4/projects/$prj 
		cp -r $TOOLS/Hammurapi-3.18.4/projects/empty/* $TOOLS/Hammurapi-3.18.4/projects/$prj
		cp -r $PROG/$prj/source/* $TOOLS/Hammurapi-3.18.4/projects/$prj/src
                cp -r $PROG/$prj/lib/* $TOOLS/Hammurapi-3.18.4/projects/$prj/lib 
                cd $TOOLS/Hammurapi-3.18.4/projects/$prj
                ant clean 
		ant -DprojectName=$prj > /dev/null 2>&1
               
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


#####################################################################################
#                              Executar ferramenta   FindBugs                       #
#                                                                                   #   
#####################################################################################

cd $PROG
find $prj/ -name *.java >  $DIR_TMP/$prj-java.txt

iniciodaexecucaodafindbugs=`date +%s`

		# Executando FindBugs
		#echo -e "\tFindBugs"
                 
		$TOOLS/findbugs-2.0.2/bin/findbugs -textui  -nested:false -effort:max -sortByClass -low  -jvmArgs "-Duser.language=pt_BR" -xml:withMessages -output $PROG/$prj/outputs/02-findbugs-$prj.xml $PROG/$prj/$prj.jar 

terminiodaexecucaodafindbugs=`date +%s`
soma3=`expr $terminiodaexecucaodafindbugs - $iniciodaexecucaodafindbugs`
resultado3=`expr 10800 + $soma3`
tempo3=`date -d @$resultado3 +%H:%M:%S`
#echo Tempo gasto para execução da FindBugs: $tempo3
info3="O-Tempo-gasto-para-execução-da-FindBugs"
info3="$info3-$tempo3"
#bash $SCRIPTS/Store.sh $prj $info3


#####################################################################################
#                              Executar ferramenta   FindBugs                       #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdafindbugs=`date +%s`

XML=$PROG/$prj/outputs/02-findbugs-$prj.xml
PROGRAMA=$prj
VERSAO=.
ARQUIVO=$prj
USER=postgres
PASSWORD=postgres
if [ `cat $XML | wc -l` -ne 0 ] 
then 
bash  $SCRIPTS/Parser_FindBugs.sh $XML $PROGRAMA $VERSAO $ARQUIVO $USER $PASSWORD $DIR_TMP 
fi


terminiodaexecucaodoparserdafindbugs=`date +%s`
soma4=`expr $terminiodaexecucaodoparserdafindbugs - $iniciodaexecucaodoparserdafindbugs`
resultado4=`expr 10800 + $soma3 + $soma4`
tempo4=`date -d @$resultado4 +%H:%M:%S`
#echo Tempo gasto para execução parser FindBugs: $tempo4 
info4="O-tempo-gasto-para-execução-parser-FindBugs"
info4="$info4-$tempo4"
info_total_findbugs="$tempo4"
echo Tempo gasto para execução da ferramenta FindBugs: $info_total_findbugs 
#bash $SCRIPTS/Store.sh $prj $info4



#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                              Executar ferramenta  JCSC                            #
#                                                                                   #   
#####################################################################################



cd $PROG/$prj

iniciodaexecucaodajcsc=`date +%s`
 mkdir $PROG/$prj/outputs/03-jcsc-$prj 
		# Encontrar lista dos fontes
		for java in $(find source -name "*.java"); do
                       
			
			# Substituindo / por - no nome
			nome=${java//\//-}

			#echo "----- Arquivo: $java -----"
			# Executando JCSC
			#echo -e "\tJCSC"

			$TOOLS/JCSC/bin/jcsc.sh -r $TOOLS/JCSC/rules/jcsc.jcsc.xml $java > $PROG/$prj/outputs/03-jcsc-$prj/03-jcsc-$nome.txt

done

terminiodaexecucaodajcsc=`date +%s`
soma5=`expr $terminiodaexecucaodajcsc - $iniciodaexecucaodajcsc`
resultado5=`expr 10800 + $soma5`
tempo5=`date -d @$resultado5 +%H:%M:%S`
#echo Tempo gasto para execução da JCSC: $tempo5
info5="O-tempo-gasto-para-execução-da-JCSC"
info5="$info5-$tempo5"
#bash $SCRIPTS/Store.sh $prj $info5

#####################################################################################
#                              Executar ferramenta  JCSC                            #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
iniciodaexecucaodoparserdajcsc=`date +%s`

for java2 in $(find source -name "*.java"); do
nome2=${java2//\//-}
TXT3=$PROG/$prj/outputs/03-jcsc-$prj/03-jcsc-$nome2.txt 
if [ `cat $TXT3 | wc -l` -ne 0 ] 
then                        
                        bash $SCRIPTS/Parser_JCSC.sh $TXT3 $prj $nome2 
fi

done

terminiodaexecucaodoparserdajcsc=`date +%s`
soma6=`expr $terminiodaexecucaodoparserdajcsc - $iniciodaexecucaodoparserdajcsc`
resultado6=`expr 10800 + $soma5 + $soma6`
tempo6=`date -d @$resultado6 +%H:%M:%S`
#echo Tempo gasto para execução parser JCSC: $tempo6 
info6="O-tempo-gasto-para-execução-parser-JCSC"
info6="$info6-$tempo6"
info_total_jcsc="$tempo6"
echo Tempo gasto para execução da ferramenta JCSC: $info_total_jcsc 
#bash $SCRIPTS/Store.sh $prj $info6

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                              Executar ferramenta  Checkstyle                      #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodacheckstyle=`date +%s`
mkdir $PROG/$prj/outputs/04-checkstyle-$prj > /dev/null 2>&1
java -jar $TOOLS/Checkstyle/checkstyle-5.6-all.jar -c $TOOLS/Checkstyle/sun_checks.xml -f xml -r $PROG/$prj/source > $PROG/$prj/outputs/04-checkstyle-$prj/04-checkstyle.xml 


terminiodaexecucaodacheckstyle=`date +%s`
soma7=`expr $terminiodaexecucaodacheckstyle - $iniciodaexecucaodacheckstyle`
resultado7=`expr 10800 + $soma7`
tempo7=`date -d @$resultado7 +%H:%M:%S`
#echo Tempo gasto para execução da Checkstyle: $tempo7
info7="O-tempo-gasto-para-execução-da-Checkstyle"
info7="$info7-$tempo7"
#bash $SCRIPTS/Store.sh $prj $info7

#####################################################################################
#                              Executar ferramenta Checkstyle                       #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdacheckstyle=`date +%s`

      
XMLLL=$PROG/$prj/outputs/04-checkstyle-$prj/04-checkstyle.xml
if [ `cat $XMLLL | wc -l` -ne 0 ] 
then
                        bash $SCRIPTS/Parser_Checkstyle2.sh $XMLLL $prj $PROG/$prj/source 
fi

terminiodaexecucaodoparserdacheckstyle=`date +%s`
soma8=`expr $terminiodaexecucaodoparserdacheckstyle - $iniciodaexecucaodoparserdacheckstyle`
resultado8=`expr 10800 + $soma7 + $soma8`
tempo8=`date -d @$resultado8 +%H:%M:%S`
#echo Tempo gasto para execução parser Checkstyle: $tempo8
info8="O-tempo-gasto-para-execução-parser-Checkstyle"
info8="$info8-$tempo8"
info_total_checkstyle="$tempo8"
echo Tempo gasto para execução da ferramenta Checkstyle: $info_total_checkstyle
#bash $SCRIPTS/Store.sh $prj $info8


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

			

#####################################################################################
#                              Executar ferramenta JLint                            #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodajlint=`date +%s`

TIPOS_AVISOS="synchronization 
deadlock 
race_condition 
wait_nosync
inheritance
super_finalize
not_overridden
field_redefined
shadow_local
data_flow
null_reference
zero_operand
zero_result
redundant
overflow
incomp_case
short_char_cmp
string_cmp
weak_cmp
domain
truncation
bounds 
"

mkdir $PROG/$prj/outputs/05-jlint-$prj > /dev/null 2>&1
for java9 in $(find source -name "*.java"); do
nome5=${java9//\//-}
class=${java9/%java/class}

			# Executando JLint
			#echo -e "\tJLint"
                        mkdir $PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome5 
                        for TYPE in $TIPOS_AVISOS 
			do jlint -all +$TYPE $class >& $PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome5/05-jlint-$nome5-$TYPE.txt 
                        done
done

terminiodaexecucaodajlint=`date +%s`
soma9=`expr $terminiodaexecucaodajlint - $iniciodaexecucaodajlint`
resultado9=`expr 10800 + $soma9`
tempo9=`date -d @$resultado9 +%H:%M:%S`
#echo Tempo gasto para execução da JLint: $tempo9
info9="O-tempo-gasto-para-execução-da-JLint"
info9="$info9-$tempo9"
#bash $SCRIPTS/Store.sh $prj $info9

#####################################################################################
#                              Executar ferramenta JLint                            #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdajlint=`date +%s`

for java10 in $(find source -name "*.java"); do
nome6=${java10//\//-}
                        for TYPE2 in $TIPOS_AVISOS                       
                        do 
TXT2=$PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome6/05-jlint-$nome6-$TYPE2.txt
if [ `cat $TXT2 | wc -l` -ne 0 ] 
then
                        
                        bash $SCRIPTS/Parser_JLint.sh $TXT2 $prj $nome6 $TYPE2 
fi
                        done

done

terminiodaexecucaodoparserdajlint=`date +%s`
soma10=`expr $terminiodaexecucaodoparserdajlint - $iniciodaexecucaodoparserdajlint`
resultado10=`expr 10800 + $soma9 + $soma10`
tempo10=`date -d @$resultado10 +%H:%M:%S`
#echo Tempo gasto para execução parser JLint: $tempo10 
info10="O-tempo-gasto-para-execução-parser-JLint"
info10="$info10-$tempo10"
info_total_jlint="$tempo10"
echo Tempo gasto para execução da ferramenta Jlint: $info_total_jlint
#bash $SCRIPTS/Store.sh $prj $info10

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                              Executar ferramenta  PMD                             #
#                                                                                   #   
#####################################################################################
zip -r $prj.zip  source/ > /dev/null 2>&1	

iniciodaexecucaodapmd=`date +%s`
			#echo -e "\tPMD"
                        mkdir $PROG/$prj/outputs/06-pmd-$prj/                       	
		        $TOOLS/PMD/bin/run.sh pmd -f xml -R rulesets/internal/all-java.xml -d $PROG/$prj/$prj.zip > $PROG/$prj/outputs/06-pmd-$prj/06-pmd-$prj.xml    
			


terminiodaexecucaodapmd=`date +%s`
soma11=`expr $terminiodaexecucaodapmd - $iniciodaexecucaodapmd`
resultado11=`expr 10800 + $soma11`
tempo11=`date -d @$resultado11 +%H:%M:%S`
#echo Tempo gasto para execução da PMD: $tempo11
info11="O-tempo-gasto-para-execução-da-PMD"
info11="$info11-$tempo11"
#bash $SCRIPTS/Store.sh $prj $info11
		

#####################################################################################
#                              Executar ferramenta  PMD                             #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdapmd=`date +%s`

XML2=$PROG/$prj/outputs/06-pmd-$prj/06-pmd-$prj.xml
sed -i 1d $XML2
if [ `cat $XML2 | wc -l` -ne 0 ] 
then
                        bash $SCRIPTS/Parser_PMD2.sh $XML2 $PROG/$prj/$prj.zip:source/ $prj $rule2 
fi
                       


terminiodaexecucaodoparserdapmd=`date +%s`
soma12=`expr $terminiodaexecucaodoparserdapmd - $iniciodaexecucaodoparserdapmd`
resultado12=`expr 10800 + $soma11 + $soma12`
tempo12=`date -d @$resultado12 +%H:%M:%S`
#echo Tempo gasto para execução parser PMD: $tempo12
info12="O-tempo-gasto-para-execução-parser-PMD"
info12="$info12-$tempo12"
info_total_pmd="$tempo12"
echo Tempo gasto para execução da ferramenta PMD: $info_total_pmd
#bash $SCRIPTS/Store.sh $prj $info12



#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################



#####################################################################################
#                              Executar ferramenta  ESC/Java                        #
#                                                                                   #   
#####################################################################################

# Redefinindo para JDK 1.5
		        export JAVA_HOME=/home/vr-pc/Dropbox/WarningsFix/tools/jdk1.5.0
                        export PATH=$JAVA_HOME/bin:$PATH


iniciodaexecucaodaescjava=`date +%s`
mkdir $PROG/$prj/outputs/07-esc-java-$prj 
for java7 in $(find source -name "*.java"); do
nome9=${java7//\//-}

                     
			# Executando ESC/Java
			#echo -e "\tESC/Java"
                        cd $TOOLS/ESCJava                       
			./escjava2 -cp $PROG/$prj/source $PROG/$prj/$java7 > $PROG/$prj/outputs/07-esc-java-$prj/07-esc-java-$nome9.txt 
                        
done

terminiodaexecucaodaescjava=`date +%s`
soma13=`expr $terminiodaexecucaodaescjava - $iniciodaexecucaodaescjava`
resultado13=`expr 10800 + $soma13`
tempo13=`date -d @$resultado13 +%H:%M:%S`
#echo Tempo gasto para execução da  ESCJava: $tempo13
info13="O-tempo-gasto-para-execução-da-ESCJava"
info13="$info13-$tempo13"

# Redefinindo para JDK 1.7
		         export JAVA_HOME=/home/vr-pc/Dropbox/WarningsFix/tools/jdk1.7.0
                         export PATH=$JAVA_HOME/bin:$PATH

#bash $SCRIPTS/Store.sh $prj $info13



#####################################################################################
#                              Executar ferramenta  ESC/Java                        #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
 
cd $PROG/$prj 
iniciodaexecucaodoparserdaescjava=`date +%s`

for java8 in $(find source -name "*.java"); do
nome10=${java8//\//-}
TXT=$PROG/$prj/outputs/07-esc-java-$prj/07-esc-java-$nome10.txt
if [ `cat $TXT | wc -l` -ne 0 ] 
then
bash $SCRIPTS/Parser_ESCJava.sh $TXT $nome10 $prj 
fi                        
done

terminiodaexecucaodoparserdaescjava=`date +%s`
soma14=`expr $terminiodaexecucaodoparserdaescjava - $iniciodaexecucaodoparserdaescjava`
resultado14=`expr 10800 + $soma13 + $soma14`
tempo14=`date -d @$resultado14 +%H:%M:%S`
#echo Tempo gasto para execução parser  ESCJava: $tempo14 
info14="O-tempo-gasto-para-execução-parser-ESCJava"
info14="$info14-$tempo14"
info_total_escjava="$tempo14"
echo Tempo gasto para execução da ferramenta ESCJava: $info_total_escjava
#bash $SCRIPTS/Store.sh $prj $info14


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

