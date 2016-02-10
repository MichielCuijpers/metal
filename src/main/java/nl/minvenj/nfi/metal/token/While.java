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
import static nl.minvenj.nfi.metal.Util.checkNotNull;

import java.io.IOException;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;

public class While extends Token {

    private final Token _op;
    private final Expression _pred;

    public While(final Token op, final Expression pred, final Encoding enc) {
        super(enc);
        _op = checkNotNull(op, "op");
        _pred = pred == null ? expTrue() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = iterate(scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc);
        if (res.succeeded()) { return new ParseResult(true, new Environment(res.getEnvironment().order.closeBranch(this), res.getEnvironment().input, res.getEnvironment().offset)); }
        return new ParseResult(false, env);
    }

    private ParseResult iterate(final String scope, final Environment env, final Encoding enc) throws IOException {
        if (!_pred.eval(env, enc)) { return new ParseResult(true, env); }
        final ParseResult res = _op.parse(scope, env, enc);
        if (res.succeeded()) { return iterate(scope, res.getEnvironment(), enc); }
        return new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + ", " + _pred + ")";
    }

}
