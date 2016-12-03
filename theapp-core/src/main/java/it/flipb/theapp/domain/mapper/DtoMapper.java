package it.flipb.theapp.domain.mapper;

import it.flipb.theapp.domain.mapper.scanner.TargetPortScannerJobMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class DtoMapper {
    private ModelMapper modelMapper;

    public DtoMapper() {
        modelMapper = new ModelMapper();
        configure();
    }

    private void configure() {
        // register converters
        modelMapper.addConverter(new TargetPortScannerJobMapper());
    }

    @NotNull
    public <S,T> T map(@NotNull S _source, @NotNull Class<T> _destination) {
       return modelMapper.map(_source, _destination);
    }
}
