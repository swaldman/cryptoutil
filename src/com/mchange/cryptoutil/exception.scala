package com.mchange.cryptoutil

class CryptoutilException( msg : String, cause : Throwable = null ) extends Exception( msg, cause )
class BadConversionException( msg : String, t : Throwable = null ) extends CryptoutilException( msg, t )
