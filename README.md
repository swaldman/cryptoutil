# cryptoutil

This ports, simplifies, and improves from [consuela](https://github.com/swaldman/consuela) a bunch of utilities that are convenient when
working on crypto (-graphic, -currency) projects, and also other binary heavy projects. It consists of...

* Convenient extension methods to `Byte`,`Int`,`String`,`Array[Byte]`, and `Seq[Byte]`
* A lightweight, typesafe framework for generating hashes (so far just `SHA_3`)

Here are some examples.

```plaintext
Welcome to Scala 3.3.1 (17.0.5, Java Java HotSpot(TM) 64-Bit Server VM).
Type in expressions for evaluation. Or try :help.
                                                                                                                                                                                                   
scala> import com.mchange.cryptoutil.{*,given}
                                                                                                                                                                                                   
scala> val helloHex = "hello".getBytes().hex
val helloHex: String = 68656c6c6f
                                                                                                                                                                                                   
scala> val helloHex = "hello".getBytes().hex0x
val helloHex: String = 0x68656c6c6f
                                                                                                                                                                                                   
scala> val helloBytes = helloHex.decodeHexToSeq
val helloBytes: scala.collection.immutable.ArraySeq[Byte] = ArraySeq(104, 101, 108, 108, 111)
                                                                                                                                                                                                   
scala> val rehello = new String(helloBytes.toByteArray)
val rehello: String = hello
                                                                                                                                                                                                   
scala> val helloHash = Hash.SHA3_256(helloBytes)
val helloHash: com.mchange.cryptoutil.Hash.SHA3_256 = ArraySeq(51, 56, -66, 105, 79, 80, -59, -13, 56, -127, 73, -122, -51, -16, 104, 100, 83, -88, -120, -72, 79, 66, 77, 121, 42, -12, -71, 32, 35, -104, -13, -110)
                                                                                                                                                                                                   
scala> val helloHashHex = helloHash.hex0x
val helloHashHex: String = 0x3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392
```

In addition to these, there are utilities for converting between `BigInt` and `Array[Byte]`, between `Seq[Byte]` and `Array[Byte]`,
generating and decoding base64 strings, and converting `BigInt` to `Int` or `Long` cautiously (throwing an Exception if the conversion
would overflow).
