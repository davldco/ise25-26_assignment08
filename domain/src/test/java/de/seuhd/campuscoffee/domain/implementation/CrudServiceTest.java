package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.objects.Pos;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CrudServiceImpl generic CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
public class CrudServiceTest {

    @Mock
    private CrudDataService<Pos, Long> dataService;

    private CrudServiceImpl<Pos, Long> createService() {
        return new CrudServiceImpl<>(Pos.class) {
            @Override
            protected CrudDataService<Pos, Long> dataService() {
                return dataService;
            }
        };
    }

    @Test
    void clearCallsDataService() {
        // given
        CrudServiceImpl<Pos, Long> service = createService();

        // when
        service.clear();

        // then
        verify(dataService).clear();
    }

    @Test
    void getByIdReturnsEntity() {
        // given
        CrudServiceImpl<Pos, Long> service = createService();
        Pos pos = TestFixtures.getPosFixtures().getFirst();
        when(dataService.getById(1L)).thenReturn(pos);

        // when
        Pos result = service.getById(1L);

        // then
        verify(dataService).getById(1L);
        assertThat(result).isEqualTo(pos);
    }

    @Test
    void upsertCreatesNewEntityWhenIdIsNull() {
        // given
        CrudServiceImpl<Pos, Long> service = createService();
        Pos posWithoutId = TestFixtures.getPosFixtures().getFirst().toBuilder().id(null).build();
        Pos posWithId = posWithoutId.toBuilder().id(1L).build();
        when(dataService.upsert(posWithoutId)).thenReturn(posWithId);

        // when
        Pos result = service.upsert(posWithoutId);

        // then
        verify(dataService).upsert(posWithoutId);
        verify(dataService, never()).getById(any());
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void upsertUpdatesExistingEntityWhenIdIsNotNull() {
        // given
        CrudServiceImpl<Pos, Long> service = createService();
        Pos existingPos = TestFixtures.getPosFixtures().getFirst();
        when(dataService.getById(1L)).thenReturn(existingPos);
        when(dataService.upsert(existingPos)).thenReturn(existingPos);

        // when
        Pos result = service.upsert(existingPos);

        // then
        verify(dataService).getById(1L);
        verify(dataService).upsert(existingPos);
        assertThat(result).isEqualTo(existingPos);
    }

    @Test
    void deleteCallsDataService() {
        // given
        CrudServiceImpl<Pos, Long> service = createService();

        // when
        service.delete(1L);

        // then
        verify(dataService).delete(1L);
    }
}
