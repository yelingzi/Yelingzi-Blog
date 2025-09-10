import type { AlbumDTO, PhotoDTO } from '@/type/album'
import request from '@/utils/request'


//添加相册封面
export const uploadAlbumCoverService = (data: FormData) => request.post('/api/admin/album/upload/cover', data)


//查看相册
export const getAlbumListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/album/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加相册
export const addAlbumService = (data: AlbumDTO) => request.post('/api/admin/album/add', data)

//删除相册
export const delAlbumService = (id: any) =>
    request.delete(`/api/admin/del/${id}`)

//更新相册
export const updateAlbumtService = (data: AlbumDTO) => request.post('/api/admin/album/update', data)

//查看一个相册
export const getAlbumByIdService = (albumID: number) => 
  request.get('/api/album/data', {
  params: {
    albumID: albumID,
  },
})

//查看相册
export const getAlbumListService = () => request.get('/api/admin/album/list')

//添加图片到相册
export const addPhotoToAlbumService = (data: PhotoDTO) => request.post('/api/admin/album/photo/add', data)