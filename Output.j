.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
invokestatic Output/read()I
 istore 0
 goto L1
L1:
invokestatic Output/read()I
 istore 1
 goto L2
L2:
L4:
 ldc 0
 istore 2
L4:
 iload 0
 iload 1
 if_icmpne L5
 goto L7
L5:
 iload 0
 iload 1
 if_icmpgt L8
 goto L9
L8:
 iload 0
 iload 1
 isub 
 istore 0
 goto L12
L12:
L11:
 goto L10
L9:
 iload 1
 iload 0
 isub 
 istore 1
 goto L14
L14:
L13:
L10:
 goto L6
L6:
 goto L4
L7:
 goto L3
L3:
 iload 0
invokestatic Output/print(I)V
 goto L15
L15:
 iload 0
 iload 1
 iload 0
 iadd 
 ldc 42
 iadd 
 ldc 2023
invokestatic Output/print(I)V
 goto L16
L16:
 ldc 114
 ldc 0
 istore 3
 istore 3
 goto L17
L17:
 ldc 114
 ldc 0
 istore 4
 istore 4
 goto L18
L18:
 ldc 114
 ldc 0
 istore 5
 istore 5
 goto L19
L19:
 iload 3
 iload 4
 iload 5
invokestatic Output/print(I)V
 goto L20
L20:
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

