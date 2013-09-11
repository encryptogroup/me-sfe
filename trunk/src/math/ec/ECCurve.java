package math.ec;

import java.math.BigInteger;
import java.util.HashMap;


/***************************************************************************************************
*
*
* This file is part of ME-SFE, a secure two-party computation framework.
*
* Copyright (c) 2012 - 2013 Wilko Henecka and Thomas Schneider
*
* ME-SFE is free software; you can redistribute it and/or modify it under the terms of the
* GNU General Public License as published by the Free Software Foundation; either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with this program.  
* If not, see <http://www.gnu.org/licenses/>.
*
* Getting Source ==============
*
* Source for this application is maintained at code.google.com, a repository for free software
* projects.
*
* For details, please see http://code.google.com/p/me-sfe/
*/


public class ECCurve {
	
	private BigInteger p, a, b, order;
	private ECPoint G;
	
	private ECCurve(BigInteger a, BigInteger b, BigInteger p, BigInteger gx, BigInteger gy, BigInteger order){
		this.a = a;
		this.b = b;
		this.p = p;
		this.order = order;
		this.G = new ECPoint(gx, gy, this);
	}
	
	public BigInteger getModulus(){
		return p;
	}
	
	public BigInteger getA(){
		return a;
	}
	
	public ECPoint getGenerator(){
		return G;
	}
	
	public BigInteger getOrder(){
		return order;
	}
	
	/**
     * Decode a point on this curve from its ASN.1 encoding. The different
     * encodings are taken account of, including point compression for
     * <code>F<sub>p</sub></code> (X9.62 s 4.2.1 pg 17).
     * @return The decoded point.
     */
    public ECPoint decodePoint(byte[] encoded){
        ECPoint p = null;

        switch (encoded[0])
        {
            // infinity
        case 0x00:
            if (encoded.length > 1)
            {
                throw new RuntimeException("Invalid point encoding");
            }
            p = new ECPoint(this);
            break;
            // compressed
        case 0x02:
        case 0x03:
            /* we need modular sqrt first...
        	int ytilde = encoded[0] & 1;
            byte[]  i = new byte[encoded.length - 1];

            System.arraycopy(encoded, 1, i, 0, i.length);

            ECFieldElement x = new ECFieldElement.Fp(this.q, new BigInteger(1, i));
            ECFieldElement alpha = x.multiply(x.square().add(a)).add(b);
            ECFieldElement beta = alpha.sqrt();

            //
            // if we can't find a sqrt we haven't got a point on the
            // curve - run!
            //
            if (beta == null)
            {
                throw new RuntimeException("Invalid point compression");
            }

            int bit0 = (beta.toBigInteger().testBit(0) ? 1 : 0);

            if (bit0 == ytilde)
            {
                p = new ECPoint.Fp(this, x, beta, true);
            }
            else
            {
                p = new ECPoint.Fp(this, x,
                    new ECFieldElement.Fp(this.q, q.subtract(beta.toBigInteger())), true);
            }
            break;
            */
            // uncompressed
        case 0x04:
            // hybrid
        case 0x06:
        case 0x07:
            byte[]  xEnc = new byte[(encoded.length - 1) / 2];
            byte[]  yEnc = new byte[(encoded.length - 1) / 2];

            System.arraycopy(encoded, 1, xEnc, 0, xEnc.length);
            System.arraycopy(encoded, xEnc.length + 1, yEnc, 0, yEnc.length);

            p = new ECPoint(new BigInteger(1, xEnc), new BigInteger(1, yEnc), this);
            break;
        default:
            throw new RuntimeException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
        }

        return p;
    }
	
	
	public static ECCurve getCurve(String name) throws Exception{
		if(!ecLib.containsKey(name)){
			throw new Exception("Curve not defined!");
		}else{
			BigInteger[] vals = ecLib.get(name);
			return new ECCurve(vals[0],vals[1],vals[2], vals[3], vals[4], vals[5]);
		}
	}
	
	private static final HashMap<String, BigInteger[]> ecLib = new HashMap<String, BigInteger[]>(5);
	static { //format:  key(String) : a,b,p,gx,gy,order(BigInteger[])
		ecLib.put("P-192", new BigInteger[]{BigInteger.valueOf(3),
				new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",16),
				new BigInteger("6277101735386680763835789423207666416083908700390324961279"),
				new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",16),
				new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16),
				new BigInteger("6277101735386680763835789423176059013767194773182842284081")});
		ecLib.put("secp256r1", new BigInteger[]{
				new BigInteger("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc",16),
				new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",16),
				new BigInteger("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff",16),
				new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16),
				new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16),
				new BigInteger("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551",16)});
		ecLib.put("secp160r1", new BigInteger[]{
				new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFC",16),
				new BigInteger("1C97BEFC54BD7A8B65ACF89F81D4D4ADC565FA45",16),
				new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF",16),
				new BigInteger("4A96B5688EF573284664698968C38BB913CBFC82",16),
				new BigInteger("23A628553168947D59DCC912042351377AC5FB32",16),
				new BigInteger("0100000000000000000001F4C8F927AED3CA752257",16)
		});
	}
	
	
/*	_EC_lib = {
	"secp224r1":(mpz("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE",16),
	            mpz("B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4",16),
	            mpz("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001",16),
	            mpz("B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21",16),
	            mpz("BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34",16),
	            mpz("FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D",16) ),
	"secp192r1":(mpz("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC",16),
	            mpz("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1",16),
	            mpz("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF",16),
	            mpz("188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012",16),
	            mpz("07192B95FFC8DA78631011ED6B24CDD573F977A11E794811",16),
	            mpz("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831",16) ),
	            }*/
	
}
