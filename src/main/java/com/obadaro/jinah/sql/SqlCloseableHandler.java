/* 
 * JINAH Project - Java Is Not A Hammer
 * http://obadaro.com/jinah
 *
 * Copyright (C) 2010-2012 Roberto Badaro 
 * and individual contributors by the @authors tag.
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
package com.obadaro.jinah.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.obadaro.jinah.common.util.Preconditions;

/**
 * @author Roberto Badaro
 */
public class SqlCloseableHandler {

    /**
     * Cache para manter os Statements registrados para a instância corrente. Os
     * mesmos serão fechados na execução do método <code>close()</code>.
     */
    private Set<Statement> statementRegister = new HashSet<Statement>(0);

    /**
     * Cache para manter os ResultSets registrados para a instância corrente. Os
     * mesmos serão fechados na execução do método <code>close()</code>.
     */
    private Set<ResultSet> resultSetRegister = new HashSet<ResultSet>(0);

    /**
     * Returns a new instance of SqlCloseableHandler.
     * 
     * @return
     */
    public static SqlCloseableHandler getService() {

        return new SqlCloseableHandler();
    }

    /**
     * Constructor.
     */
    public SqlCloseableHandler() {
        // noop.
    }

    /**
     * Verifies pending resources to close before execute super.finalize(). Is not expected that we
     * have resources to close here. If it occurs, a developer forgot to call
     * {@link SqlCloseableHandler#close()}.
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {

        if ((statementRegister != null && statementRegister.size() > 0)
                || (resultSetRegister != null && resultSetRegister.size() > 0)) {

            final Logger logger = Logger.getLogger("global");
            logger.warning("Cleaning your garbage. Somebody forgot to explicitly close statements/resultSets.");

            close();
        }

        statementRegister = null;
        resultSetRegister = null;

        super.finalize();
    }

    /**
     * Registra o resultset para ser finalizado na execução do método
     * <code>close()</code>.
     * 
     * @param rs
     * @return Retorna o ResultSet registrado.
     */
    public ResultSet add(final ResultSet rs) {

        Preconditions.checkArgument(rs != null, "rs");
        resultSetRegister.add(rs);
        return rs;
    }

    /**
     * Registra o resultset para ser finalizado na execução do método
     * <code>close()</code>.
     * 
     * @param rs
     *            ResultSet
     * @param registraStatement
     *            Se <code>true</code>, registra o Statement que gerou o
     *            ResultSet informado se o mesmo for retornado pelo método
     *            <code>{@link ResultSet#getStatement()}</code>.
     * @return
     */
    public ResultSet add(final ResultSet rs, final boolean registraStatement) {

        add(rs);

        if (registraStatement) {
            Statement stOrigem = null;
            try {
                stOrigem = rs.getStatement();
            } catch (final SQLException e) {
                throw new JinahSqlException("I can't retrieve the Statement that produces the ResultSet.", e);
            }

            if (stOrigem != null) {
                statementRegister.add(stOrigem);
            }
        }

        return rs;
    }

    /**
     * Registra o statement para ser finalizado na execução do método
     * <code>close()</code>.
     * 
     * @param st
     * @return Retorna o Statement registrado.
     */
    public Statement add(final Statement st) {

        Preconditions.checkArgument(st != null, "st");
        statementRegister.add(st);
        return st;
    }

    /**
     * Closes all registered ResultSet and Statement.
     */
    public void close() {

        try {
            closeResultSets();
        } catch (final Exception e) {
            // noop.
        }

        try {
            closeStatements();
        } catch (final Exception e) {
            // noop.
        }
    }

    /**
     * Remove o statement do cache, evitando o fechamento automático do mesmo.
     * <p>
     * Deve-se usar com parcimônia. Os casos onde não se pretende o fechamento
     * automático é quando o Statement for objeto de retorno de um método, por
     * exemplo, ficando a cargo do método chamador cuidar da finalização do
     * ciclo de vida do recurso.
     * </p>
     * 
     * @param st
     */
    public void ignore(final Statement st) {

        if (st != null) {
            statementRegister.remove(st);
        }
    }

    /**
     * Remove o ResultSet do cache, evitando o fechamento automático do mesmo.
     * <p>
     * Deve-se usar com parcimônia. Os casos onde não se pretende o fechamento
     * automático é quando o ResultSet for objeto de retorno de um método, por
     * exemplo, ficando a cargo do método chamador cuidar da finalização do
     * ciclo de vida do recurso.
     * </p>
     * 
     * @param rs
     */
    public void ignore(final ResultSet rs) {

        if (rs != null) {
            resultSetRegister.remove(rs);
        }
    }

    /**
     * Closes registered Statements.
     */
    protected void closeStatements() {

        for (final Statement st : statementRegister) {
            if (st != null) {
                try {
                    st.close();
                } catch (final SQLException e) {
                    // noop.
                }
            }
        }

        statementRegister.clear();
    }

    /**
     * Closes registered ResultSets.
     */
    protected void closeResultSets() {

        for (final ResultSet rs : resultSetRegister) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (final SQLException e) {
                    // noop.
                }
            }
        }

        resultSetRegister.clear();
    }

}