#!/bin/bash

export JAVA_HOME='/Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home'

# Generate the private key for signing
#   CN: Ali Baba
#   OU: IT
#   O : Lumberjack
#   L : Brussels
#   ST: Brussels
#   C : BE
$JAVA_HOME/bin/keytool -genkeypair -alias sesame -keystore lumberjack-private.keystore -storepass changeit -keyalg DSA -keysize 2048 -sigalg SHA256withDSA -validity 36524 -dname 'CN=Ali Baba, OU=IT, O=Lumberjack, L=Brussels, ST=Brussels, C=BE'

# Verify the private key
$JAVA_HOME/bin/keytool -list -v -keystore lumberjack-private.keystore -storepass changeit

# Export the public key
$JAVA_HOME/bin/keytool -export -keystore lumberjack-private.keystore -alias sesame -file sesame.cer -storepass 'changeit' -rfc

# Import the public key in another keystore
$JAVA_HOME/bin/keytool -import -v -alias sesame -file sesame.cer -keystore lumberjack-public.keystore -storepass changeit
