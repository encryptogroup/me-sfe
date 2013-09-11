package math.ec;

import java.math.BigInteger;


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


public class ECPoint {

	private static final BigInteger TWO = BigInteger.valueOf(2);
	private static final BigInteger THREE = BigInteger.valueOf(3);
	private ECCurve curve;
	private BigInteger p, x, y;
	private boolean infinity;

	public ECPoint(BigInteger x, BigInteger y, ECCurve curve) {
		this(x, y, curve, false);
	}

	/* constructs infinity point */
	public ECPoint(ECCurve curve) {
		this(null, null, curve, true);
	}

	private ECPoint(BigInteger x, BigInteger y, ECCurve curve, boolean infinity) {
		this.curve = curve;
		this.p = curve.getModulus();
		this.x = x;
		this.y = y;
		this.infinity = infinity;
	}

	public boolean isInfinity() {
		return infinity;
	}

	public ECPoint add(ECPoint other) {
		if (infinity) {
			return other;
		}
		if (other.infinity) {
			return this;
		}
		if (this.equals(other)) {
			return this.multiply(TWO);
		}
		BigInteger d = this.x.subtract(other.x);
		if (d.equals(BigInteger.ZERO)) {
			return new ECPoint(curve);
		}
		BigInteger s = (y.subtract(other.y)).multiply(d.modInverse(p)).mod(p);
		BigInteger xn = s.modPow(TWO, p).subtract(x).subtract(other.x).mod(p);
		BigInteger yn = x.subtract(xn).multiply(s).subtract(y).mod(p);
		return new ECPoint(xn, yn, curve);
	}

	public ECPoint subtract(ECPoint other) {
		return add(new ECPoint(other.x, other.y.negate().mod(other.p), other.curve, other.infinity));
	}

	public ECPoint multiply(BigInteger k) {
		if (infinity) {
			return new ECPoint(curve);
		}
		if (k.equals(BigInteger.ONE)) {
			return this;
		}
		if (k.equals(TWO)) {
			if (y.equals(BigInteger.ZERO)) {
				return new ECPoint(curve);
			}
			BigInteger s = x.modPow(TWO, p).multiply(BigInteger.valueOf(3)).add(curve.getA())
					.multiply((y.multiply(TWO)).modInverse(p)).mod(p);
			BigInteger xn = s.modPow(TWO, p).subtract(TWO.multiply(x)).mod(p);
			BigInteger yn = x.subtract(xn).multiply(s).subtract(y).mod(p);
			return new ECPoint(xn, yn, curve);
		}
		BigInteger[] pc = new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE };
		for (int i = k.bitLength() - 1; i >= 0; i--) {
			pc = doubleP(pc);
			if (k.testBit(i)) {
				pc = addP(pc);
			}
		}
		BigInteger xn = pc[0].multiply(pc[2].modPow(TWO, p).modInverse(p)).mod(p);
		BigInteger yn = pc[1].multiply(pc[2].modPow(THREE, p).modInverse(p)).mod(p);
		return new ECPoint(xn, yn, curve);
	}

	private BigInteger[] addP(BigInteger[] pc) {
		if (pc[0].equals(BigInteger.ZERO) && pc[1].equals(BigInteger.ZERO)
				&& pc[2].equals(BigInteger.ONE)) {
			return new BigInteger[] { x, y, BigInteger.ONE };
		}
		BigInteger tmp1 = pc[2].modPow(TWO, p);
		BigInteger tmp2 = tmp1.multiply(pc[2]).mod(p);
		BigInteger A = tmp1.multiply(x).mod(p);
		BigInteger B = tmp2.multiply(y).mod(p);
		BigInteger C = A.subtract(pc[0]);
		BigInteger D = B.subtract(pc[1]);
		tmp1 = C.modPow(TWO, p);
		tmp2 = tmp1.multiply(C).mod(p); //C.modPow(THREE,p)
		BigInteger xn = D.modPow(TWO, p).subtract(tmp2.add(TWO.multiply(pc[0]).multiply(tmp1)))
				.mod(p);
		BigInteger yn = D.multiply(pc[0].multiply(tmp1).subtract(xn))
				.subtract(pc[1].multiply(tmp2)).mod(p);
		BigInteger zn = pc[2].multiply(C).mod(p);
		return new BigInteger[] { xn, yn, zn };
	}

	private BigInteger[] doubleP(BigInteger[] pc) {
		if (pc[0].equals(BigInteger.ZERO) && pc[1].equals(BigInteger.ZERO)
				&& pc[2].equals(BigInteger.ONE)) {
			return new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE }; // infinity
		}
		BigInteger tmp1 = pc[1].modPow(TWO, p);
		BigInteger tmp2 = tmp1.multiply(tmp1).mod(p);
		BigInteger A = BigInteger.valueOf(4).multiply(pc[0]).multiply(tmp1);
		BigInteger B = BigInteger.valueOf(8).multiply(tmp2);
		tmp1 = pc[2].modPow(TWO, p);
		BigInteger C = THREE.multiply(pc[0].subtract(tmp1)).multiply(
				pc[0].add(tmp1));
		BigInteger D = A.multiply(BigInteger.valueOf(-2)).add(C.modPow(TWO, p)).mod(p);
		BigInteger xn = D;
		BigInteger yn = C.multiply(A.subtract(D)).subtract(B).mod(p);
		BigInteger zn = TWO.multiply(pc[1].multiply(pc[2])).mod(p);
		return new BigInteger[] { xn, yn, zn };
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof ECPoint) {
			ECPoint op = (ECPoint) other;
			return this.p.equals(op.p) && this.x.equals(op.x) && this.y.equals(op.y)
					&& this.infinity == op.infinity;
		} else
			return false;
	}

	public String toString() {
		return "x: " + x.toString(2) + "\ny: " + y.toString(2);
	}

	/**
	 * return the field element encoded with point compression. (S 4.3.6)
	 */
	public byte[] getEncoded() {
		if (this.isInfinity()) {
			return new byte[1];
		}

		int qLength = (p.bitLength() + 7) / 8; // bytes per element

		if (false) // with compression
		{
			byte PC;

			if (y.testBit(0)) {
				PC = 0x03;
			} else {
				PC = 0x02;
			}

			byte[] X = integerToBytes(x, qLength);
			byte[] PO = new byte[X.length + 1];

			PO[0] = PC;
			System.arraycopy(X, 0, PO, 1, X.length);

			return PO;
		} else // no compression
		{
			byte[] X = integerToBytes(x, qLength);
			byte[] Y = integerToBytes(y, qLength);
			byte[] PO = new byte[X.length + Y.length + 1];

			PO[0] = 0x04;
			System.arraycopy(X, 0, PO, 1, X.length);
			System.arraycopy(Y, 0, PO, X.length + 1, Y.length);

			return PO;
		}
	}

	public static byte[] integerToBytes(BigInteger s, int qLength) {
		byte[] bytes = s.toByteArray();

		if (qLength < bytes.length) {
			byte[] tmp = new byte[qLength];
			System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
			return tmp;
		} else if (qLength > bytes.length) {
			byte[] tmp = new byte[qLength];
			System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
			return tmp;
		}
		return bytes;
	}
}


