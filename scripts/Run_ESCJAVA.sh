
 DIR_TMP=~/workspace/WarningsFIX/tmp	
 PROG=~/workspace/WarningsFIX/programs_analyzeds
 prj=$1
 TOOLS=~/workspace/WarningsFIX/tools
 SCRIPTS=~/workspace/WarningsFIX/scripts
 PARSER=~/workspace/WarningsFIX/parser

cd $PROG/$prj


#####################################################################################
#                              Executar ferramenta  ESC/Java                        #
#                                                                                   #   
#####################################################################################

# Redefinindo para JDK 1.5
		        export JAVA_HOME=/home/vr-pc/Dropbox/WarningsFIX/tools/jdk1.5.0
                        export PATH=$JAVA_HOME/bin:$PATH


iniciodaexecucaodaescjava=`date +%s`
mkdir $PROG/$prj/outputs/07-esc-java-$prj 
pwd
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
		        export JAVA_HOME=$TOOLS/jdk1.7.0
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