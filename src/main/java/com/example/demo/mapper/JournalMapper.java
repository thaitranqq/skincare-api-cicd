package com.example.demo.mapper;

import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalPhotoDTO;
import com.example.demo.model.JournalEntry;
import com.example.demo.model.JournalPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JournalMapper {

    @Mapping(source = "photos", target = "photoIds")
    JournalEntryDTO toJournalEntryDTO(JournalEntry journalEntry);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    JournalEntry toJournalEntry(JournalEntryCreateRequest request);

    @Mapping(source = "journalEntry.id", target = "entryId")
    JournalPhotoDTO toJournalPhotoDTO(JournalPhoto journalPhoto);

    default List<Long> mapPhotosToIds(List<JournalPhoto> photos) {
        if (photos == null || photos.isEmpty()) {
            return Collections.emptyList();
        }
        return photos.stream()
                .map(JournalPhoto::getId)
                .collect(Collectors.toList());
    }
}
