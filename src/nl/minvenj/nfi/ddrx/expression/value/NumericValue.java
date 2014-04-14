/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.minvenj.nfi.ddrx.expression.value;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class NumericValue extends Value {

    private final BigInteger _numericValue;

    public NumericValue(String name, byte[] data, Encoding encoding) {
        super(name, data, encoding);
        _numericValue = new BigInteger(data);
    }

    public NumericValue(BigInteger value, Encoding encoding) {
        super(compact(value.toByteArray()), encoding);
        _numericValue = value;
    }

    public NumericValue(long value, Encoding encoding) {
        super(compact(value), encoding);
        _numericValue = encoding.isSigned() ? BigInteger.valueOf(value) : new BigInteger(1, ByteBuffer.allocate(8).putLong(value).array());
    }

    public NumericValue operation(NumericValueOperation op) {
        return op.execute(getNumericValue());
    }

    @Override
    public int compareTo(Value other) {
        if (other instanceof NumericValue) {
            return getNumericValue().compareTo(((NumericValue) other).getNumericValue());
        }
        return super.compareTo(other);
    }

    public BigInteger getNumericValue() {
        return _numericValue;
    }

    private static byte[] compact(byte[] in) {
        if (in.length < 2) {
            return in;
        }
        int i = 0;
        for (; i < in.length && in[i] == 0; i++);
        byte[] out = new byte[in.length - i];
        System.arraycopy(in, i, out, 0, out.length);
        return out;
    }

    private static byte[] compact(long v) {
        return compact(ByteBuffer.allocate(8).putLong(v).array());
    }

}
