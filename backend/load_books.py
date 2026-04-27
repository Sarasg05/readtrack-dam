import os
import json
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from api.models import Book, Author, Genre

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
JSON_PATH = os.path.join(BASE_DIR, 'books.json')

with open(JSON_PATH, encoding='utf-8') as f:
    data = json.load(f)

for item in data:

    # 1. Autor (no duplicar)
    author_obj, _ = Author.objects.get_or_create(
        name=item['author']
    )

    # 2. Libro (EVITAR duplicados por título)
    book, created = Book.objects.get_or_create(
        title=item['title'],
        defaults={
            'author': author_obj,
            'total_pages': item['total_pages'],
            'synopsis': item.get('synopsis', ''),
            'cover': item.get('cover', '')
        }
    )

    if not created:
        print(f"Ya existe: {book.title}")
        continue

    # 3. Géneros
    for g in item['genres']:
        genre_obj, _ = Genre.objects.get_or_create(name=g)
        book.genres.add(genre_obj)

    print(f"Añadido: {book.title}")

print("Carga completada")