package com.mchange.cryptoutil

import java.math.BigInteger

import scala.collection.immutable

import com.mchange.lang.{ByteUtils,IntegerUtils,LongUtils}

// derived from consuela
// https://github.com/swaldman/consuela/blob/master/src/main/scala/com/mchange/sc/v1/consuela/package.scala

// javadocs: "Instances of Base64.Encoder class are safe for use by multiple concurrent threads."
private val B64Encoder = java.util.Base64.getEncoder()
private val B64UrlEncoder = java.util.Base64.getUrlEncoder()

// javadocs: "Instances of Base64.Decoder class are safe for use by multiple concurrent threads."
private val B64Decoder = java.util.Base64.getDecoder()
private val B64UrlDecoder = java.util.Base64.getUrlDecoder()

given Byteable[Array[Byte]]              = Byteable.ofByteArray
given Byteable[Seq[Byte]]                = Byteable.ofSeqByte
given Byteable[immutable.ArraySeq[Byte]] = Byteable.ofArraySeqByte

extension ( byte : Byte ) def hex : String = ByteUtils.toLowercaseHexAscii( byte ) 

extension ( string : String )
  def decodeHexToArray : Array[Byte] =
    val hexstring = if string.startsWith( "0x" ) then string.substring(2) else string
    ByteUtils.fromHexAscii( hexstring )
  def decodeHexToSeq : immutable.ArraySeq[Byte] = immutable.ArraySeq.unsafeWrapArray( decodeHexToArray )
  def decodeBase64 : Array[Byte] = B64Decoder.decode( string )
  def decodeBase64url : Array[Byte] = B64UrlDecoder.decode( string )
  def isMixedCase : Boolean = string.exists( Character.isUpperCase ) && string.exists( Character.isLowerCase )

extension ( bi : BigInt )
  /**
   * Converts the byte representation
   * of the BigInt to the desired len by removing or padding
   * with leading zeros.
   *
   * If `allowCoerceNegativeValues` is set, ignores sign.
   */
  def unsignedBytes( len : Int, allowCoerceNegativeValues : Boolean = false ) : Array[Byte] =
    asFixedLengthUnsignedByteArray( bi, len, allowCoerceNegativeValues )
  def toValidLong : Long =
    if ( bi.isValidLong ) bi.toLong else throw new BadConversionException( s"BigInt ${bi} cannot be converted to Long without truncation" )
  def toValidInt : Int =
    if ( bi.isValidInt ) bi.toInt else throw new BadConversionException( s"BigInt ${bi} cannot be converted to Int without truncation" )

extension ( jbi : BigInteger )
  def toBigInt : BigInt = BigInt(jbi)
  
  // try this again when we're ready to update to Scala 3.7.x+
  // def unsignedBytes( len : Int, allowCoerceNegativeValues : Boolean = false ) : Array[Byte] = toBigInt.unsignedBytes(len, allowCoerceNegativeValues)

  def toValidLong : Long = toBigInt.toValidLong
  def toValidInt : Int = toBigInt.toValidInt

extension ( i : Int )
  def toByteArrayBigEndian : Array[Byte] = IntegerUtils.byteArrayFromInt( i )
  def toByteSeqBigEndian : immutable.ArraySeq[Byte] = immutable.ArraySeq.unsafeWrapArray( i.toByteArrayBigEndian )
  def fillBigEndian( bytes : Array[Byte], offset : Int ) : Unit = IntegerUtils.intIntoByteArray( i, offset, bytes )
  def fillBigEndian( bytes : Array[Byte] ) : Unit = IntegerUtils.intIntoByteArray( i, 0, bytes )

extension ( l : Long )
  def toByteArrayBigEndian : Array[Byte] = LongUtils.byteArrayFromLong( l )
  def toByteSeqBigEndian : immutable.ArraySeq[Byte] = immutable.ArraySeq.unsafeWrapArray( l.toByteArrayBigEndian )
  def fillBigEndian( bytes : Array[Byte], offset : Int ) : Unit = LongUtils.longIntoByteArray( l, offset, bytes )
  def fillBigEndian( bytes : Array[Byte] ) : Unit =  LongUtils.longIntoByteArray( l, 0, bytes )

def asFixedLengthUnsignedByteArray( bi : BigInt, desiredLength : Int, allowCoerceNegativeValues : Boolean = false ) : Array[Byte] =
  if bi.signum < 0 && !allowCoerceNegativeValues then
    throw new BadConversionException( s"Treating negative BigInt ${bi} as an unsigned value in conversion to fixed-length byte array." )

  val bytes = bi.toByteArray
  val len = bytes.length
  if len == desiredLength then
    bytes
  else
    val minimized = bytes.dropWhile( _ == 0 )
    val minimizedLength = minimized.length
    if minimizedLength == desiredLength then
      minimized
    else if minimizedLength < desiredLength then
      zeroPad( minimized, minimizedLength, desiredLength )
    else
      throw new IllegalArgumentException( s"BigInt '${bi}' requires a representation larger than the desired length of ${desiredLength}." )

def asFixedLengthSignedByteArray( bi : BigInt, desiredLength : Int ) : Array[Byte] =
  val bytes = bi.toByteArray
  val len = bytes.length
  if len == desiredLength then
    bytes
  else
    val neg = bi.signum < 0
    val minimized = if ( neg ) bytes.dropWhile( _ == 1 ) else bytes.dropWhile( _ == 0 )
    val minimizedLength = minimized.length
    if minimizedLength == desiredLength then
      minimized
    else if minimizedLength < desiredLength then
      if neg then negOnePad( minimized, minimizedLength, desiredLength ) else zeroPad( minimized, minimizedLength, desiredLength )
    else
      throw new IllegalArgumentException( s"BigInt '${bi}' requires a representation larger than the desired length of ${desiredLength}." )

def zeroPadLeft( bytes : Array[Byte], desiredLength : Int ) : Array[Byte] =
  val len = bytes.length
  require( len <= desiredLength )
  zeroPad( bytes, len, desiredLength )

def negOnePadLeft( bytes : Array[Byte], desiredLength : Int ) : Array[Byte] =
  val len = bytes.length
  require( len <= desiredLength )
  negOnePad( bytes, len, desiredLength )

private def zeroPad( bytes : Array[Byte], bytesLength : Int, desiredLength : Int ) : Array[Byte] =
  val out = Array.ofDim[Byte]( desiredLength ) // we're relying on the fact that the default Byte value is zero
  Array.copy( bytes, 0, out, desiredLength - bytesLength, bytesLength )
  out

private def negOnePad( bytes : Array[Byte], bytesLength : Int, desiredLength : Int ) : Array[Byte] =
  val out = Array.fill[Byte]( desiredLength )( -1 )
  Array.copy( bytes, 0, out, desiredLength - bytesLength, bytesLength )
  out

private[cryptoutil] def _hex( ba : Array[Byte] ) : String =
  ByteUtils.toLowercaseHexAscii( ba ); // should we switch to the DatatypeConverter implementation of hex encoding/decoding?

private[cryptoutil] def _hex0x( ba : Array[Byte] ) : String =
  "0x" + _hex(ba)

private[cryptoutil] def arr[T]( t : T )(using Byteable[T]) = summon[Byteable[T]].toByteArray(t)

private[cryptoutil] def xor( _bytes : Array[Byte], other : Array[Byte] ) : Array[Byte] =
  require( _bytes.length == other.length, s"We can only xor sequences or arrays of the same length. [_bytes.length: ${_bytes.length}, other.length: ${other.length}]" )
  (0 until _bytes.length).map( i => (_bytes(i) ^ other(i)).toByte ).toArray
