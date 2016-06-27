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

package io.parsingdata.metal.token;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

public class RepN extends Token {

    private final Token _op;
    private final ValueExpression _n;

    public RepN(final Token op, final ValueExpression n, final Encoding enc) {
        super(enc);
        _op = checkNotNull(op, "op");
        _n = checkNotNull(n, "n");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList count = _n.eval(env, enc);
        if (count.size != 1) { throw new RuntimeException("Count must yield a single value."); }
        if (!count.head.isPresent()) { return new ParseResult(false, env); }
        final ParseResult res = iterate(scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc, count.head.get().asNumeric().longValue());
        if (res.succeeded()) { return new ParseResult(true, new Environment(res.getEnvironment().order.closeBranch(), res.getEnvironment().input, res.getEnvironment().offset)); }
        return new ParseResult(false, env);
    }

    private ParseResult iterate(final String scope, final Environment env, final Encoding enc, final long count) throws IOException {
        if (count <= 0) { return new ParseResult(true, env); }
        final ParseResult res = _op.parse(scope, env, enc);
        if (res.succeeded()) { return iterate(scope, res.getEnvironment(), enc, count - 1); }
        return new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + "," + _n + ")";
    }

}
