package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.exception.FindingEntityException;
import by.zuevvlad.wialontransport.dao.exception.InjectionParameterException;
import by.zuevvlad.wialontransport.dao.exception.NoAvailableConnectionInPoolException;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.Optional.empty;
import static java.util.logging.Logger.getLogger;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

////TODO: перечитать все тесты + доделать остальные
//@RunWith(MockitoJUnitRunner.class)
//public final class AbstractEntityRepositoryTest {
//
//    @Mock
//    private DataBaseConnectionPool mockedDataBaseConnectionPool;
//
//    @Mock
//    private ResultRowMapper<User> mockedUserResultRowMapper;
//
//    @Mock
//    private ResultSetMapper<User> mockedUserResultSetMapper;
//
//    @Mock
//    private FounderGeneratedId<Long> mockedFounderGeneratedId;
//
//    @Captor
//    private ArgumentCaptor<String> stringArgumentCaptor;
//
//    @Captor
//    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;
//
//    @Captor
//    private ArgumentCaptor<Connection> connectionArgumentCaptor;
//
//    @Captor
//    private ArgumentCaptor<Long> longArgumentCaptor;
//
//    private EntityRepository<User> repository;
//
//    @Before
//    public void initializeRepository() {
//        this.repository = new UserRepository(this.mockedDataBaseConnectionPool, this.mockedUserResultRowMapper,
//                this.mockedUserResultSetMapper, this.mockedFounderGeneratedId,
//                getLogger(UserRepository.class.getName()));
//    }
//
//    @Test
//    public void userShouldBeFoundById()
//            throws SQLException {
//        final Connection givenConnection = mock(Connection.class);
//        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(Optional.of(givenConnection));
//
//        final PreparedStatement givenPreparedStatement = mock(PreparedStatement.class);
//        when(givenConnection.prepareStatement(anyString())).thenReturn(givenPreparedStatement);
//
//        final ResultSet givenResultSet = mock(ResultSet.class);
//        when(givenResultSet.next()).thenReturn(true);
//        when(givenPreparedStatement.executeQuery()).thenReturn(givenResultSet);
//
//        final long givenId = 255;
//        final User expected = new User(givenId, "email", "password");
//        when(this.mockedUserResultRowMapper.map(any(ResultSet.class))).thenReturn(expected);
//
//        final Optional<User> optionalActual = this.repository.findById(givenId);
//        assertTrue(optionalActual.isPresent());
//        final User actual = optionalActual.get();
//        assertEquals(expected, actual);
//
//        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
//        verify(givenConnection, times(1)).prepareStatement(this.stringArgumentCaptor.capture());
//        verify(givenPreparedStatement, times(1)).setLong(anyInt(), this.longArgumentCaptor.capture());
//        verify(givenPreparedStatement, times(1)).executeQuery();
//        verify(givenResultSet, times(1)).next();
//        verify(this.mockedUserResultRowMapper, times(1)).map(this.resultSetArgumentCaptor.capture());
//        verify(this.mockedDataBaseConnectionPool, times(1))
//                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
//
//        assertEquals(UserRepository.QUERY_TO_SELECT_BY_ID, this.stringArgumentCaptor.getValue());
//        assertEquals(givenId, this.longArgumentCaptor.getValue().longValue());
//        assertSame(givenResultSet, this.resultSetArgumentCaptor.getValue());
//        assertSame(givenConnection, this.connectionArgumentCaptor.getValue());
//    }
//
//    @Test(expected = NoAvailableConnectionInPoolException.class)
//    public void userShouldNotBeFoundByIdBecauseOfNoAvailableConnectionInPool() {
//        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
//
//        this.repository.findById(255);
//
//        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
//    }
//
//    @Test(expected = FindingEntityException.class)
//    public void userShouldNotBeFoundBecauseOfExceptionDuringPreparingStatement()
//            throws SQLException {
//        final Connection givenConnection = mock(Connection.class);
//        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(Optional.of(givenConnection));
//
//        when(givenConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
//
//        this.repository.findById(255);
//
//        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
//        verify(givenConnection, times(1)).prepareStatement(this.stringArgumentCaptor.capture());
//        verify(this.mockedDataBaseConnectionPool, times(1))
//                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
//
//        assertEquals(UserRepository.QUERY_TO_SELECT_BY_ID, this.stringArgumentCaptor.getValue());
//        assertSame(givenConnection, this.connectionArgumentCaptor.getValue());
//    }
//
//    @Test(expected = FindingEntityException.class)
//    public void userShouldNotBeFoundByIdBecauseOfExceptionDuringSettingIdInPreparedStatement()
//            throws SQLException {
//        final Connection givenConnection = mock(Connection.class);
//        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(Optional.of(givenConnection));
//
//        final PreparedStatement givenPreparedStatement = mock(PreparedStatement.class);
//        when(givenConnection.prepareStatement(anyString())).thenReturn(givenPreparedStatement);
//
//        doThrow(SQLException.class).when(givenPreparedStatement).setLong(anyInt(), anyLong());
//
//        final long givenId = 255;
//        this.repository.findById(givenId);
//
//        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
//        verify(givenConnection, times(1)).prepareStatement(this.stringArgumentCaptor.capture());
//        verify(givenPreparedStatement, times(1)).setLong(anyInt(), this.longArgumentCaptor.capture());
//        verify(this.mockedDataBaseConnectionPool, times(1))
//                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
//
//        assertEquals(UserRepository.QUERY_TO_SELECT_BY_ID, this.stringArgumentCaptor.getValue());
//        assertEquals(givenId, this.longArgumentCaptor.getValue().longValue());
//        assertSame(givenConnection, this.connectionArgumentCaptor.getValue());
//    }
//
//    @Test(expected = FindingEntityException.class)
//    public void userShouldNotBeFoundBeIdBecauseOfExceptionDuringExecutionQuery()
//            throws SQLException {
//        final Connection givenConnection = mock(Connection.class);
//        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(Optional.of(givenConnection));
//
//        final PreparedStatement givenPreparedStatement = mock(PreparedStatement.class);
//        when(givenConnection.prepareStatement(anyString())).thenReturn(givenPreparedStatement);
//
//        when(givenPreparedStatement.executeQuery()).thenThrow(SQLException.class);
//
//        final long givenId = 255;
//        this.repository.findById(givenId);
//
//        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
//        verify(givenConnection, times(1)).prepareStatement(this.stringArgumentCaptor.capture());
//        verify(givenPreparedStatement, times(1)).setLong(anyInt(), this.longArgumentCaptor.capture());
//        verify(givenPreparedStatement, times(1)).executeQuery();
//        verify(this.mockedDataBaseConnectionPool, times(1))
//                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
//
//        assertEquals(UserRepository.QUERY_TO_SELECT_BY_ID, this.stringArgumentCaptor.getValue());
//        assertEquals(givenId, this.longArgumentCaptor.getValue().longValue());
//        assertSame(givenConnection, this.connectionArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void userShouldNotBeFoundByIdBecauseOfExceptionDuringCheckingExistingNextInResultSet()
//            throws SQLException {
//
//    }
//
//    private static final class UserRepository extends AbstractEntityRepository<User> {
//        private static final String QUERY_TO_SELECT_BY_ID = "SELECT users.id, users.email, users.password FROM users "
//                + "WHERE users.id = ?";
//        private static final String QUERY_TO_SELECT_ALL = "SELECT users.id, users.email, users.password FROM users";
//
//        private static final String QUERY_TO_INSERT = "INSERT INTO users(email, password) VALUES(?, ?)";
//        private static final int PARAMETER_INDEX_EMAIL_IN_QUERY_TO_INSERT = 1;
//        private static final int PARAMETER_INDEX_PASSWORD_IN_QUERY_TO_INSERT = 2;
//
//        private static final String QUERY_TO_UPDATE = "UPDATE users SET email = ?, password = ? WHERE users.id = ?";
//        private static final int PARAMETER_INDEX_EMAIL_IN_QUERY_TO_UPDATE = 1;
//        private static final int PARAMETER_INDEX_PASSWORD_IN_QUERY_TO_UPDATE = 2;
//        private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE = 3;
//
//        private static final String QUERY_TO_DELETE_BY_ID = "DELETE FROM users WHERE users.id = ?";
//
//        public UserRepository(final DataBaseConnectionPool dataBaseConnectionPool,
//                              final ResultRowMapper<User> userResultRowMapper,
//                              final ResultSetMapper<User> userResultSetMapper,
//                              final FounderGeneratedId<Long> founderGeneratedId,
//                              final Logger logger) {
//            super(dataBaseConnectionPool, userResultRowMapper, userResultSetMapper, founderGeneratedId, logger);
//        }
//
//        @Override
//        protected String findQueryToSelectById() {
//            return QUERY_TO_SELECT_BY_ID;
//        }
//
//        @Override
//        protected String findQueryToSelectAll() {
//            return QUERY_TO_SELECT_ALL;
//        }
//
//        @Override
//        protected String findQueryToInsertEntity() {
//            return QUERY_TO_INSERT;
//        }
//
//        @Override
//        protected void injectParametersInInsertingPreparedStatement(final PreparedStatement preparedStatement,
//                                                                    final User insertedUser) {
//            try {
//                preparedStatement.setString(PARAMETER_INDEX_EMAIL_IN_QUERY_TO_INSERT, insertedUser.email);
//                preparedStatement.setString(PARAMETER_INDEX_PASSWORD_IN_QUERY_TO_INSERT, insertedUser.password);
//            } catch (final SQLException cause) {
//                throw new InjectionParameterException(cause);
//            }
//        }
//
//        @Override
//        protected String findQueryToUpdateEntity() {
//            return QUERY_TO_UPDATE;
//        }
//
//        @Override
//        protected void injectParametersInUpdatingPreparedStatement(final PreparedStatement preparedStatement,
//                                                                   final User updatedUser) {
//            try {
//                preparedStatement.setString(PARAMETER_INDEX_EMAIL_IN_QUERY_TO_UPDATE, updatedUser.getEmail());
//                preparedStatement.setString(PARAMETER_INDEX_PASSWORD_IN_QUERY_TO_UPDATE, updatedUser.getPassword());
//                preparedStatement.setLong(PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE, updatedUser.getId());
//            } catch (final SQLException cause) {
//                throw new InjectionParameterException(cause);
//            }
//        }
//
//        @Override
//        protected String findQueryToDeleteEntityById() {
//            return QUERY_TO_DELETE_BY_ID;
//        }
//    }
//
//    private static final class User extends Entity {
//        private static final String NOT_DEFINED_EMAIL = "not defined";
//        private static final String NOT_DEFINED_PASSWORD = "not defined";
//
//        private String email;
//        private String password;
//
//        public User() {
//            this.email = NOT_DEFINED_EMAIL;
//            this.password = NOT_DEFINED_PASSWORD;
//        }
//
//        public User(final long id, final String email, final String password) {
//            super(id);
//            this.email = email;
//            this.password = password;
//        }
//
//        public void setEmail(final String email) {
//            this.email = email;
//        }
//
//        public String getEmail() {
//            return this.email;
//        }
//
//        public void setPassword(final String password) {
//            this.password = password;
//        }
//
//        public String getPassword() {
//            return this.password;
//        }
//
//        @Override
//        public boolean equals(final Object otherObject) {
//            if (!super.equals(otherObject)) {
//                return false;
//            }
//            final User other = (User) otherObject;
//            return Objects.equals(this.email, other.email) && Objects.equals(this.password, other.password);
//        }
//
//        @Override
//        public int hashCode() {
//            return super.hashCode() + Objects.hash(this.email, this.password);
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + "[email = " + this.email + ", password = " + this.password + "]";
//        }
//    }
//}
