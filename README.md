handy-android
=============

## Key signatures

### Production Key

    keytool --exportcert -list -v -alias HandyProdKey -keystore ./app/handy-prod-key.keystore
    Enter keystore password:
    Alias name: HandyProdKey
    Creation date: Oct 24, 2014
    Entry type: PrivateKeyEntry
    Certificate chain length: 1
    Certificate[1]:
    Owner: CN=Webster Ross, OU=Mobile, O=Handy, L=New York, ST=New York, C=US
    Issuer: CN=Webster Ross, OU=Mobile, O=Handy, L=New York, ST=New York, C=US
    Serial number: 3a5af983
    Valid from: Fri Oct 24 13:03:40 EDT 2014 until: Tue Mar 11 13:03:40 EDT 2042
    Certificate fingerprints:
         MD5:  C0:6B:EA:9A:73:A6:AB:51:6F:12:F7:D2:CA:E6:92:35
         SHA1: B6:FF:32:BF:CA:74:77:55:C0:A8:7E:30:E7:16:71:D3:02:7F:88:9A
         SHA256: F5:C8:8F:40:54:99:CB:81:A5:EC:90:12:A9:DF:D0:C7:54:F3:F2:33:3C:92:64:65:F4:67:86:F2:37:28:3C:84
         Signature algorithm name: SHA256withRSA
         Version: 3

    Extensions:

    #1: ObjectId: 2.5.29.14 Criticality=false
    SubjectKeyIdentifier [
    KeyIdentifier [
    0000: 25 E3 21 EE 15 00 70 54   E3 28 7A 4D 95 7B 05 4D  %.!...pT.(zM...M
    0010: 78 52 8C EE                                        xR..
    ]
    ]


### Staging Key

    keytool --exportcert -list -v -alias HandyStageKey -keystore ./app/handy-stage-key.keystore
    Enter keystore password:
    Alias name: HandyStageKey
    Creation date: Oct 23, 2014
    Entry type: PrivateKeyEntry
    Certificate chain length: 1
    Certificate[1]:
    Owner: CN=Webster Ross, OU=Mobile, O=Handy, L=New York, ST=New York, C=US
    Issuer: CN=Webster Ross, OU=Mobile, O=Handy, L=New York, ST=New York, C=US
    Serial number: 4af0e2b6
    Valid from: Thu Oct 23 17:26:55 EDT 2014 until: Mon Mar 10 17:26:55 EDT 2042
    Certificate fingerprints:
         MD5:  37:2C:DF:FF:96:50:69:D9:EE:F6:B2:B3:4D:FE:97:A1
         SHA1: E3:F9:5D:3D:4A:7D:33:CB:74:EC:8A:93:8D:15:31:4D:28:37:E2:F1
         SHA256: 43:8F:B2:02:99:2E:12:49:74:2C:FB:1F:E9:98:B6:F7:6D:91:85:EC:79:A8:E1:FD:2D:E6:09:4C:FE:2D:62:75
         Signature algorithm name: SHA256withRSA
         Version: 3

    Extensions:

    #1: ObjectId: 2.5.29.14 Criticality=false
    SubjectKeyIdentifier [
    KeyIdentifier [
    0000: 06 D3 5C 1B 18 D2 C1 E1   9D 8D 8B 76 92 CC E0 C4  ..\........v....
    0010: DE 01 A7 31                                        ...1
    ]
    ]

### Debug Key

    keytool --exportcert -list -v -alias handy-debug-key -keystore ./app/handy-debug-key.keystore
    Enter keystore password:
    Alias name: handy-debug-key
    Creation date: Mar 14, 2016
    Entry type: PrivateKeyEntry
    Certificate chain length: 1
    Certificate[1]:
    Owner: CN=Ondrej Dolejsi, OU=Mobile Team, O=Handy, L=NYC, ST=NY, C=US
    Issuer: CN=Ondrej Dolejsi, OU=Mobile Team, O=Handy, L=NYC, ST=NY, C=US
    Serial number: 56e72754
    Valid from: Mon Mar 14 17:04:20 EDT 2016 until: Wed Feb 19 16:04:20 EST 2116
    Certificate fingerprints:
         MD5:  15:5C:18:0E:0D:94:BB:22:52:0A:71:D5:7A:69:E3:18
         SHA1: 63:02:63:26:BB:58:95:B0:63:B1:61:6E:56:8D:2E:F1:22:1E:C0:15
         SHA256: A6:35:D6:69:03:43:CF:A1:91:63:CD:93:84:36:C0:DF:5C:DF:21:CA:E0:E5:57:7E:2F:1B:FD:B5:E1:CD:2F:C8
         Signature algorithm name: SHA1withRSA
         Version: 3



