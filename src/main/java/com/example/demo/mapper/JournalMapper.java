package com.example.demo.mapper;

import com.example.demo.journal.dto.JournalEntryCreateRequest;
import com.example.demo.journal.dto.JournalEntryDTO;
import com.example.demo.journal.dto.JournalPhotoDTO;
import com.example.demo.model.JournalEntry;
import com.example.demo.model.JournalPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface JournalMapper {

    @Mapping(source = "photos", target = "photos", qualifiedByName = "photosToPhotoDTOs")
    JournalEntryDTO toJournalEntryDTO(JournalEntry journalEntry);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    JournalEntry toJournalEntry(JournalEntryCreateRequest request);

    @Mapping(source = "journalEntry.id", target = "entryId")
    @Mapping(source = "fileKey", target = "imageUrl")
    JournalPhotoDTO toJournalPhotoDTO(JournalPhoto journalPhoto);

    @Named("photosToPhotoDTOs")
    default List<JournalPhotoDTO> mapPhotosToPhotoDTOs(List<JournalPhoto> photos) {
        if (photos == null || photos.isEmpty()) {
            return Collections.emptyList();
        }
        return photos.stream()
                .map(this::toJournalPhotoDTO)
                .toList();
    }
}
