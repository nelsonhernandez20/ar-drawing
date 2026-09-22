-- AniMemes premium (28 imágenes de ~/Downloads/animemes-premium)
--
-- 1) En Storage → bucket público `templates` → carpeta `premium/`
--    sube animemes-premium-01.jpg … animemes-premium-28.jpg
--
-- 2) Inserta las filas:
insert into public.images (id, title, thumbnail_url, image_url, is_premium, category, sort_order) values
(
  'animemes-premium-01',
  'AniMeme Premium 1',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-01.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-01.jpg',
  true, 'animemes', 201
),
(
  'animemes-premium-02',
  'AniMeme Premium 2',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-02.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-02.jpg',
  true, 'animemes', 202
),
(
  'animemes-premium-03',
  'AniMeme Premium 3',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-03.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-03.jpg',
  true, 'animemes', 203
),
(
  'animemes-premium-04',
  'AniMeme Premium 4',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-04.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-04.jpg',
  true, 'animemes', 204
),
(
  'animemes-premium-05',
  'AniMeme Premium 5',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-05.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-05.jpg',
  true, 'animemes', 205
),
(
  'animemes-premium-06',
  'AniMeme Premium 6',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-06.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-06.jpg',
  true, 'animemes', 206
),
(
  'animemes-premium-07',
  'AniMeme Premium 7',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-07.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-07.jpg',
  true, 'animemes', 207
),
(
  'animemes-premium-08',
  'AniMeme Premium 8',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-08.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-08.jpg',
  true, 'animemes', 208
),
(
  'animemes-premium-09',
  'AniMeme Premium 9',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-09.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-09.jpg',
  true, 'animemes', 209
),
(
  'animemes-premium-10',
  'AniMeme Premium 10',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-10.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-10.jpg',
  true, 'animemes', 210
),
(
  'animemes-premium-11',
  'AniMeme Premium 11',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-11.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-11.jpg',
  true, 'animemes', 211
),
(
  'animemes-premium-12',
  'AniMeme Premium 12',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-12.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-12.jpg',
  true, 'animemes', 212
),
(
  'animemes-premium-13',
  'AniMeme Premium 13',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-13.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-13.jpg',
  true, 'animemes', 213
),
(
  'animemes-premium-14',
  'AniMeme Premium 14',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-14.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-14.jpg',
  true, 'animemes', 214
),
(
  'animemes-premium-15',
  'AniMeme Premium 15',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-15.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-15.jpg',
  true, 'animemes', 215
),
(
  'animemes-premium-16',
  'AniMeme Premium 16',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-16.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-16.jpg',
  true, 'animemes', 216
),
(
  'animemes-premium-17',
  'AniMeme Premium 17',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-17.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-17.jpg',
  true, 'animemes', 217
),
(
  'animemes-premium-18',
  'AniMeme Premium 18',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-18.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-18.jpg',
  true, 'animemes', 218
),
(
  'animemes-premium-19',
  'AniMeme Premium 19',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-19.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-19.jpg',
  true, 'animemes', 219
),
(
  'animemes-premium-20',
  'AniMeme Premium 20',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-20.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-20.jpg',
  true, 'animemes', 220
),
(
  'animemes-premium-21',
  'AniMeme Premium 21',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-21.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-21.jpg',
  true, 'animemes', 221
),
(
  'animemes-premium-22',
  'AniMeme Premium 22',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-22.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-22.jpg',
  true, 'animemes', 222
),
(
  'animemes-premium-23',
  'AniMeme Premium 23',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-23.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-23.jpg',
  true, 'animemes', 223
),
(
  'animemes-premium-24',
  'AniMeme Premium 24',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-24.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-24.jpg',
  true, 'animemes', 224
),
(
  'animemes-premium-25',
  'AniMeme Premium 25',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-25.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-25.jpg',
  true, 'animemes', 225
),
(
  'animemes-premium-26',
  'AniMeme Premium 26',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-26.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-26.jpg',
  true, 'animemes', 226
),
(
  'animemes-premium-27',
  'AniMeme Premium 27',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-27.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-27.jpg',
  true, 'animemes', 227
),
(
  'animemes-premium-28',
  'AniMeme Premium 28',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-28.jpg',
  'https://bjxfbegekqvgeskoulsu.supabase.co/storage/v1/object/public/templates/premium/animemes-premium-28.jpg',
  true, 'animemes', 228
)
on conflict (id) do update set
  title = excluded.title,
  thumbnail_url = excluded.thumbnail_url,
  image_url = excluded.image_url,
  is_premium = excluded.is_premium,
  category = excluded.category,
  sort_order = excluded.sort_order;
