#####################################################
#####    inserir dados da  JCSC no BD           #####  
#####################################################
#echo inserindo dados programa $2 da ferramenta JCSC no BD

JARS_DIR=$WARNINGSFIX_HOME/parser/JCSC/lib 			   
cd $JARS_DIR

JARS="";

for JAR in $(ls *.jar)
do JARS=$JARS_DIR/$JAR:$JARS
done

cd $WARNINGSFIX_HOME/parser/JCSC/bin
CLASSPATH=$CLASSPATH:$JARS
java -cp $CLASSPATH  br.ufg.inf.jcsc.Parser $1 $3 $2   

#####################################################
#####           Terminando                      #####  
#####################################################
