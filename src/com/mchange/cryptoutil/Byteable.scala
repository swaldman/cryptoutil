package com.mchange.cryptoutil

import scala.collection.immutable

import java.math.BigInteger

import com.mchange.lang.ByteUtils

object Byteable:
  val ofByteArray : Byteable[Array[Byte]] = new Byteable[Array[Byte]]:
    extension (bytes : Array[Byte]) def toByteArray : Array[Byte] = bytes
    def fromByteArray( bytes : Array[Byte] ) : Array[Byte] = bytes
  val ofSeqByte : Byteable[Seq[Byte]] = new Byteable[Seq[Byte]]:
    extension ( bytes : Seq[Byte] ) def toByteArray : Array[Byte] = bytes.toArray
    def fromByteArray( bytes : Array[Byte] ) : Seq[Byte] = immutable.ArraySeq.unsafeWrapArray(bytes)

trait Byteable[T]:
  extension (t : T) def toByteArray : Array[Byte]
  def fromByteArray( bytes : Array[Byte] ) : T

  extension( t : T )( using Byteable[T] )
    def toSeq                : Seq[Byte]  = immutable.ArraySeq.unsafeWrapArray[Byte]( t.toByteArray )
    def base64               : String     = B64Encoder.encodeToString( t.toByteArray )
    def hex                  : String     = ByteUtils.toLowercaseHexAscii( t.toByteArray ); // should we switch to the DatatypeConverter implementation of hex encoding/decoding?
    def hex0x                : String     = "0x" + hex
    def toBigInteger         : BigInteger = new BigInteger( t.toByteArray )
    def toUnsignedBigInteger : BigInteger = new BigInteger( 1, t.toByteArray )
    def toBigInt             : BigInt     = BigInt( toBigInteger )
    def toUnsignedBigInt     : BigInt     = BigInt( toUnsignedBigInteger )
    def ^( other : T ) : T = fromByteArray(xor(t.toByteArray,arr(other)))


private def arr[T]( t : T )(using Byteable[T]) = summon[Byteable[T]].toByteArray(t)

private def xor( _bytes : Array[Byte], other : Array[Byte] ) : Array[Byte] =
  require( _bytes.length == other.length, s"We can only xor sequences or arrays of the same length. [_bytes.length: ${_bytes.length}, other.length: ${other.length}]" )
  (0 until _bytes.length).map( i => (_bytes(i) ^ other(i)).toByte ).toArray


