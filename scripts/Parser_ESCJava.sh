#####################################################
#####    inserir dados do ESCJAVA no BD        #####  
#####################################################
#echo inserindo dados programa $3 e classe $2 da ferramenta ESCJava no BD

JARS_DIR=$WARNINGSFIX_HOME/parser/ESCJava/lib 
DIR_TMP=$WARNINGSFIX_HOME/tmp					   
cd $JARS_DIR



JARS="";

for JAR in $(ls *.jar) 
do JARS=$JARS_DIR/$JAR:$JARS
done


cd $WARNINGSFIX_HOME/parser/ESCJava/bin
CLASSPATH=$CLASSPATH:$JARS
java -cp $CLASSPATH  br.ufg.inf.vev.Parser $1 $2 $3 
#####################################################
#####           Terminando                      #####  
#####################################################
