package io.dexproject.achatservice.generic.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperUtils {

    private static final ModelMapper modelMapper;

    /**
     * Les paramètres de propriété du mappeur de modèle sont spécifiés dans le bloc suivant.
     * La stratégie de correspondance de propriété par défaut est définie sur Strict, voir {@link MatchingStrategies}
     * Les mappages personnalisés sont ajoutés à l'aide de {@link ModelMapper#addMappings(PropertyMap)}
     */
    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * Cacher de l'usage public.
     */
    private ObjectMapperUtils() {
    }

    /**
     * <p>Remarque : l'objet outClass doit avoir un constructeur par défaut sans argument</p>
     *
     * @param <D>      type d'objet résultat.
     * @param <T>      type d'objet source à partir duquel mapper.
     * @param entity   entité qui doit être cartographiée.
     * @param outClass classe d’objet de résultat.
     * @return nouvel objet de type <code>outClass</code>.
     */
    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    /**
     * <p>Remarque : l'objet outClass doit avoir un constructeur par défaut sans argument</p>
     *
     * @param entityList liste des entités qui doivent être mappées
     * @param outCLass   classe de l'élément de liste de résultats
     * @param <D>        type d'objets dans la liste de résultats
     * @param <T>        type d'entité dans <code>entityList</code>
     * @return liste des objets mappés de type <code><D></code>.
     */
    public static <D, T> List<D> mapAll(final List<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }

    /**
     * Mappe {@code source} à {@code destination}.
     *
     * @param source      objet à partir duquel mapper
     * @param destination objet à mapper
     */
    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
