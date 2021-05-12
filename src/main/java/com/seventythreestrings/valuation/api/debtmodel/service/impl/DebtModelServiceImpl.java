package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.SortOrder;
import com.seventythreestrings.valuation.api.debtmodel.model.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.repository.DebtModelRepository;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class DebtModelServiceImpl implements DebtModelService {
    private final DebtModelRepository repository;

    @Override
    public List<DebtModel> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<DebtModel> getAllPaginatedWithGeneralDetailsAndCashflow(String sortField, SortOrder sortOrder, int pageNumber, int pageSize) {
        Sort sort = sortOrder.equals(SortOrder.ASC) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return repository.findAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @SneakyThrows
    @Override
    public DebtModel get(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
    }

    @Override
    public DebtModel create(DebtModel model) {
        return repository.save(model);
    }

    @Override
    public DebtModel update(DebtModel model) {
        return repository.save(model);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void save(DebtModel model) {
        repository.save(model);
    }
}
