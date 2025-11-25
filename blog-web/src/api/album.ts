import request from '@/utils/request'

export const getAlbumListService = () => request.get('/api/album/list')

export const getSimpleAlbumOfPhotoCountService = () => request.get('/api/album/photo/count')

export const getAlbumDataService = (album: number) => request.get('/api/album/data',
  {
    params: {
      albumID: album,
    },
  }
)

export const getBgListService = () => request.get('/api/background/list')
