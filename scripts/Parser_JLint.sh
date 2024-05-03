#####################################################
#####    inserir dados da  JLint no BD          #####  
#####################################################
#echo inserindo dados programa $2 da ferramenta JLint no BD

JARS_DIR=$WARNINGSFIX_HOME/parser/Jlint/lib 
DIR_TMP=$WARNINGSFIX_HOME/tmp					   
cd $JARS_DIR

JARS="";

for JAR in $(ls *.jar)
do JARS=$JARS_DIR/$JAR:$JARS
done

cd $WARNINGSFIX_HOME/parser/Jlint/bin
CLASSPATH=$CLASSPATH:$JARS
java -cp $CLASSPATH  br.ufg.inf.jlint.Parser $1 $2 $3 $4  

#####################################################
#####           Terminando                      #####  
#####################################################
