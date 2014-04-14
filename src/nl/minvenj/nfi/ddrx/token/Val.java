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

package nl.minvenj.nfi.ddrx.token;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;

public class Val<T extends Value> implements Token {

    private final String _name;
    private final ValueExpression<NumericValue> _size;
    private final Expression _pred;
    private final Class<T> _type;
    private final Encoding _encoding;

    public Val(String name, ValueExpression<NumericValue> size, Expression pred, Class<T> type) {
        this(name, size, pred, type, null);
    }

    public Val(String name, ValueExpression<NumericValue> size, Expression pred, Class<T> type, Encoding encoding) {
        _name = name;
        _size = size;
        _pred = pred;
        _type = type;
        _encoding = encoding;
    }

    @Override
    public boolean parse(Environment env) {
        final byte[] data = new byte[_size.eval(env).getNumericValue().intValue()];
        env.mark();
        try {
            if (env.read(data) != data.length) {
                env.reset();
                return false;
            }
            final Encoding encoding = _encoding == null ? env.getEncoding() : _encoding;
            env.put(_type.getConstructor(new Class<?>[] { String.class, byte[].class, Encoding.class }).newInstance(new Object[] { _name, data, encoding }));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
        if (_pred.eval(env)) {
            env.clear();
            return true;
        } else {
            env.reset();
            return false;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }

}
