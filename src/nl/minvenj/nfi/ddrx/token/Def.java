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

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.OptionalValue;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;

public class Def extends Token {

    private final String _name;
    private final ValueExpression _size;
    private final Expression _pred;

    public Def(String name, ValueExpression size, Expression pred, Encoding enc) {
        super(enc);
        _name = name;
        _size = size;
        _pred = pred;
    }

    public Def(String name, ValueExpression size, Expression pred) {
        this(name, size, pred, null);
    }

    @Override
    protected boolean parseImpl(String name, Environment env, Encoding enc) throws IOException {
        final OptionalValue size = _size.eval(env);
        if (!size.isPresent()) { return false; }
        final byte[] data = new byte[size.get().asNumeric().intValue()];
        env.mark();
        if (env.read(data) != data.length) {
            env.reset();
            return false;
        }
        env.put(new Value(name, _name, data, enc));
        final boolean ret = _pred.eval(env);
        if (ret) {
            env.clear();
        } else {
            env.reset();
        }
        return ret;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }

}
