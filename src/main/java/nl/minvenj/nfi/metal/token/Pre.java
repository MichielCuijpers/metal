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

package nl.minvenj.nfi.metal.token;

import static nl.minvenj.nfi.metal.Shorthand.expTrue;

import java.io.IOException;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;

public class Pre extends Token {

    private final Token _op;
    private final Expression _pred;
    private final boolean _onFalsePrecondition;

    public Pre(final Token op, final Expression pred, final boolean onFalsePrecondition, final Encoding enc) {
        super(enc);
        if (op == null) { throw new IllegalArgumentException("Argument op may not be null."); }
        _op = op;
        _pred = pred == null ? expTrue() : pred;
        _onFalsePrecondition = onFalsePrecondition;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        if (!_pred.eval(env, enc)) { return new ParseResult(_onFalsePrecondition, env); }
        return _op.parse(env, enc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + ", " + _pred + ")";
    }

}